package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
	public static String ops = "+*/@";
	public static String delims1 = "/t*+-/()]";		
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	/**
		
     **/
    	String tempexp= expr.replaceAll("\\[", "_I_AM_GROOT_[ "); //funny statement to replace all spaces with a statement
    	StringTokenizer s = new StringTokenizer(tempexp, delims);
    	
    	while(s.hasMoreTokens()) {
    		// Extract out the token
    		String var = s.nextToken().trim();
    		// Validate whether or not this is a variable or a number
    		if (!Character.isDigit(var.charAt(0))) {
    			if(!var.equals("")) {
    				if(var.contains("_I_AM_GROOT_")) { //replace whereever there is a space
    					var = var.replace("_I_AM_GROOT_", "");
    					if(!arrays.contains(new Array(var))) {
    						arrays.add(new Array(var));
    					}
    				} else {
    					int i=0;
    					while(i < vars.size() && var.length()<vars.get(i).name.length()){
    						i++;
    					}
    					if(!vars.contains(new Variable(var))) {
							vars.add(i, new Variable(var));
    					}
					}
    			}
    		}
    	}
  
    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    
    
    private static float simple(String op, float A, float B) {
    	//simple arthimetic calcuations method
        float ans = 0;
            if(op.equals("+")){
                ans = A+B;
           
            }
            else if(op.equals("@")){
                ans = A-B;
              
            }
            else if(op.equals("*")){
                ans = A*B;
                
            }
            else if(op.equals("/")){
                ans = A/B;
                
            }
    
        return ans;
    }
    
    private static String splitBrackets(String exp, int beg) {
    	int count = 1;
    	int index=beg+1; 
    	while(count > 0 && index < exp.length()) {
    		if (exp.charAt(index) == '[') { // Increase C
                count++;
            }
    		if(exp.charAt(index)==']') {
    			count--;
    	}
    		index++;
    	}
    	String sub= exp.substring(beg, index);
    	return sub;
    }
    
    private static String parensplit(String exp, int beg) {
    	int index = beg; 
    	while(index<exp.length()) {
    		
    		if(exp.charAt(index)==')') {
    			break;
    		}
    		index++;
    	}
    	String sub= exp.substring(beg, index);
    	return sub;
    }
    
    private static String convert(String exp, String operation1, String operation2) {
    	String op= operation1;
    	while (exp.contains(operation1) || exp.contains(operation2)) {
    	if(exp.contains(operation1)&& exp.contains(operation2)) {
    		 if (exp.indexOf(operation2) < exp.indexOf(operation1)) {
                 op = operation2;
                 break;
             	}
    		 else {
    			 op=operation1;
    			 break;
    		 }
    		}
    	else {
    		if(exp.contains(operation1)) {
    			op = operation1;
    			break;
    			}
    		
    		if(exp.contains(operation2)) {
    			op=operation2; 
    			 break;
    			}
    		}
    	}
       	// First, we split exp into two parts -> left = everything to the left of operation1, right = everything to the right of operation1
    	String[] sections = exp.split("\\" + op);
    	for (int i = 0; i < sections.length; i++) {
    	
    	}
    	StringTokenizer startTok = new StringTokenizer(sections[0],ops);
    	String start = "";
    	while(startTok.hasMoreTokens()) {
    		start = startTok.nextToken().trim();
    	}
    	
    	StringTokenizer endTok = new StringTokenizer(sections[1],ops);
    	String end = endTok.nextToken();
    	float answer = simple(op, Float.parseFloat(start), Float.parseFloat(end));
    	
    	exp = exp.replace(start + op + end, "" + answer);
    	return exp;
    	
    }
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	//REMOVE WHITESPACE
    	String exp= expr.replaceAll(" ", "");
    	

    	for(int i=0; i<vars.size(); i++) {
    		while(exp.contains(vars.get(i).name)) {
    			
    			exp=exp.replace(vars.get(i).name, "" + vars.get(i).value);
    			
    		}
    	}
    	
		//if there is only a variable 
    	if((!exp.contains("+") || !exp.contains("-") || !exp.contains("/") || !exp.contains("*") || !exp.contains("[") || exp.contains("(")) && (Character.isLetter(exp.charAt(0)))){
    		int i; 
    		for(i=0; i<vars.size(); i++) {
    			if(vars.get(i).name.equals(exp)) {
    			return vars.get(i).value;
    			}
    		}	
    	}

    	//You need to go through an array list and find the value of it 
    	for (int i = 0; i < arrays.size(); i++) {
    		while(exp.contains((arrays.get(i).name))) {
    			//you need this line bc you need it in your replace function
    			String exptemp1 = splitBrackets(exp,exp.indexOf(arrays.get(i).name)+ arrays.get(i).name.length());
    			String exptemp= "" + exptemp1; 
    			 if (exptemp.startsWith("[")) {
    				 exptemp = exptemp.substring(1);
                 }
                 if (exptemp.endsWith("]")) {
                	 exptemp = exptemp.substring(0, exptemp.length() - 1);
                 }
                 
                
                	 exp=exp.replace(arrays.get(i).name+exptemp1, "" + arrays.get(i).values[(int)evaluate(exptemp, vars, arrays)]);
                	 
                
    			//make a substring that will access the inside of the arraylist to find the index        
    		}
    	}
    	
    	if (exp.contains("-")) {
            exp = exp.replace("-", "@");
           //replacing = with @ in order to process negative values
        }
    	
    	//find () and have it go through exp
    	while(exp.contains("(")) {
    		String exptemp2= parensplit(exp,exp.lastIndexOf("("));
    		String parentemp= exptemp2;
    		if (parentemp.startsWith("(")) {
				 parentemp = parentemp.substring(1);
            }
            if (parentemp.endsWith(")")) {
            	parentemp = parentemp.substring(0, parentemp.length() - 1);
            }
            
            exp = exp.replace(exptemp2 + ")", "" + evaluate(parentemp, vars, arrays));
            
    	}
    	

    	 // Evaluate the integer expression
        while(exp.contains("*")||exp.contains("/")) {
        	exp = convert(exp, "*", "/");
        	
        }
        
        while(exp.contains("+")||exp.contains("@")) {
        	
        	exp = convert(exp, "+", "@");
        	
        }
        
        return Float.parseFloat(exp);

    	//return Float.parseFloat(expression);
    	//if expression doesnt have any operator or open bracket or if the first thing is a parenthesis
    	//THEN you return the expression as a float (parseFloat)
    	
    	//if its not an operator or open bracket float value = varible at whatever index
	
    	
    }
}
