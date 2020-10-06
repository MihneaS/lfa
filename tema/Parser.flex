import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

%%
 
%class Parser
%line
%int
%{
    enum State {
        NO_STATE, INITIALIZATION
    }

    String equal = "=";
    HashMap<String, Integer> var_to_value = new HashMap<String, Integer>();
    Stack<String> AExprs = new Stack<String>();
    Stack<String> ops = new Stack<String>();
    String var_to_change = null;
    State state = State.INITIALIZATION;

    void init_var(String new_var) {
        var_to_value.put(new_var, null);
    }
    void on_new_int(String new_int) {
        if (AExprs.size() < 1) {
            AExprs.push(new_int);
            return;
        } else {
            String last = AExprs.pop();
            String op = ops.pop();
            if (op.equals("+")) {
                int a = Integer.parseInt(last);
                int b = Integer.parseInt(new_int);
                AExprs.push(Integer.toString(a+b));
            }
        }
    }

    void on_new_plus(String yytext) {
        ops.push(yytext);
    }

    void on_new_var(String new_var) {
        if (state == State.INITIALIZATION) {
            init_var(new_var);
        } else if (state == State.NO_STATE) {
            var_to_change = new_var;
        } else {
            Integer a = var_to_value.get(new_var);
            if( a == null) {
                // TODO treat error
            } else {
                AExprs.push((Integer.toString(a)));
            }
        }
    }

    void evaluate_AExpr(String AExpr) {
        System.out.println("a");
    }

    void on_end_cmd() {
        state = State.NO_STATE;
        var_to_value.put(var_to_change, Integer.valueOf(AExprs.pop()));
    }
    
    void finish() {
        for (Map.Entry<String, Integer> entry: var_to_value.entrySet()) {
            String key = entry.getKey();
            Integer val = entry.getValue();
            System.out.println(key + "=" + val);
        }
    }
%}

%eof{
	finish();
%eof}

integer = [1-9][0-9]* | 0
str = [a-z][a-z]*
var = str
Aval = integer
BVal = True | False
initialization = "int"
%%

"+" {
    on_new_plus(yytext());
}

{var} {
    on_new_var(yytext());
}

{Aval} {
    on_new_int(yytext());
}

; {
    on_end_cmd();
}

. {}


