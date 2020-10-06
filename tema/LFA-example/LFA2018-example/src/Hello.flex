import java.util.*;
import java.util.Stack;

%%
 
%class HelloLexer
%line
%int
%{

  	Stack<Expression> stack = new Stack<>();

  	Expression get_nth_element_from_stack(int element_number) {
        Stack<Expression> temp_stack = new Stack<>();

        if (element_number > stack.size()) {
            return null;
        }

        for (int j = 0; j < element_number; ++j) {
            temp_stack.push(stack.pop());
        }

        Expression res = temp_stack.peek();

        for (int j = 0; j < element_number; ++j) {
            stack.push(temp_stack.pop());
        }
        return res;
    }

	// ma uit cu maxim 3 pozitii in spate pe stiva sa vad ce element am si
	// construiesc o noua expresie (lista, cons sau concat) in functie de el
    Expression coolList() {
    	if (stack.peek() instanceof Par) {
   			return new EmptyList(); // pot avea doar lista vida
    	}
    	if (get_nth_element_from_stack(2) instanceof Par) { // lista cu un element
    		ArrayList<Expression> ls = new ArrayList<>();
    		ls.add(stack.pop());
    		return new ExprList(ls);
    	}
    	Expression e = get_nth_element_from_stack(3);
    	if (e == null || !(e instanceof Symbol)) {
    		// inseamna ca ma aflu in interiorul unei liste
    		ArrayList<Expression> ls = new ArrayList<>();
    		while (true) {
    			Expression x = stack.peek();
    			if (x instanceof Par)
    				break;
    			ls.add(stack.pop());
    		}
    		Collections.reverse(ls);
    		return new ExprList(ls);
    	}
    	// voi avea concat sau cons daca nu am intrat pe if-ul de sus
    	Symbol s = (Symbol) e;
    	Expression e1 = stack.pop();
    	Expression e2 = stack.pop();
    	stack.pop();
    	if (s.symbol().equals("++")) {
    		return new Concat(e2, e1);
    	} else {
    		return new Cons(e1, (Number) e2);
    	}
    }
%}

integer = [1-9][0-9]* | 0
concat = "++"
cons = ":"
open_par = "("
close_par = ")"
 
%%   

{open_par}    { stack.push(new Par()); }
{concat}     { stack.push(new Symbol("++")); }
{cons}     { stack.push(new Symbol(":")); }
{integer}     { stack.push(new Number(yytext())); }
{close_par}     {
    Expression x = coolList(); // scot ce am pe stiva pana dau de "(" si formez elementul
    stack.pop(); // Scoatem "(" de pe stiva
    stack.push(x); // pun elementul nou format la loc pe stiva
}
. {}


