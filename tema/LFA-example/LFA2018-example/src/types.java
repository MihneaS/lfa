
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

abstract interface Expression {
	String show();
	Expression interpret();
};

class EmptyList implements Expression {
	@Override
	public String show() {
		return "EmptyList\n";
	}

	@Override
	public Expression interpret() {
		return this;
	}
	
}

class ExprList implements Expression {
	ArrayList<Expression> elems;

	public ExprList(ArrayList<Expression> elems) {
		super();
		this.elems = elems;
	}

	@Override
	public String show() {
		String build = "";
		build += "List\n";
		
		String print = "";
		for (Expression e : elems) {
				print += e.show();
		}
		
		build += Parser.addNewline(print); 
		
		return build;
	}

	@Override
	public Expression interpret() {
		// interpretez toate elementele listei, pentru a avea o lista de liste
		ArrayList<Expression> newls = new ArrayList<Expression>();
		for (Expression e : elems) {
			newls.add(e.interpret());
		}
		return new ExprList(newls);
	}
};

class Number implements Expression {
	String number;

	public Number(String number) {
		super();
		this.number = number;
	}

	@Override
	public String show() {
		return number + "\n";
	}

	@Override
	public Expression interpret() {
		return this;
	}
	
};
class Par implements Expression {

	@Override
	public String show() {
		return null;
	}

	@Override
	public Expression interpret() {
		return null;
	}
};
class Symbol implements Expression {
	String symbol;

	public Symbol(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	String symbol() {
		return symbol;
	}

	@Override
	public String show() {
		return null;
	}

	@Override
	public Expression interpret() {
		return null;
	}
	
};
class Concat implements Expression {
	Expression e1, e2;

	public Concat(Expression e1, Expression e2) {
		super();
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	public String show() {
		String build = "";
		build += "Concat\n";
		
		String print = "";
		print += e1.show() + e2.show();
		
		build += Parser.addNewline(print); 
		return build;
	}
	@Override
	public Expression interpret() {
		// interpretez cele doua expresii din interior pentru a obtine liste
		ExprList ea = (ExprList) e1.interpret();
		ExprList eb = (ExprList) e2.interpret();
		ArrayList<Expression> el = ea.elems;
		el.addAll(eb.elems);
		
		return new ExprList(el);
	} 
	
};
class Cons implements Expression {
	Expression e;
	Number n;
	public Cons(Expression e, Number n) {
		super();
		this.e = e;
		this.n = n;
	}
	@Override
	public String show() {
		String build = "";
		build += "Cons\n";
		
		String print = "";
		print += n.show() + e.show();
		
		build += Parser.addNewline(print); 
		return build;
	}
	@Override
	public Expression interpret() {
		// interpretez expresia din interior pentru a obtine lista
		ExprList ea = (ExprList) e.interpret();
		ArrayList<Expression> el = ea.elems;
		el.add(0, n);
		
		return new ExprList(el);
	} 
	
};


