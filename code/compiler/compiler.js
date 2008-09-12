load("stdmob.js");

// 
// Tokeniser state
//

token_c = " ";

//
// Tokeniser
// 
next_token = function() {
	var c = token_c;
	var val = undefined;
	var token;

	var is_num = function(c) {
		return "0" <= c && c <= "9";
	};

	var is_alphanum = function(c) {
		return ("0" <= c && c <= "9") || c === "_" || ("a" <= c && c <= "z") || ("A" <= c && c <= "Z");
	};

	var is_symb = function(c) {
		return c === "=" || c === "!" || c === "<" || c === "&" || c === "|";
	};

	var str = [];

	while(c === " " || c === "\n" || c === "\t") {
		c = getch();
	}
	if(c === "\"") {
		c = getch();
		while(c !== undefined && c !== "\"") {
			if(c === "\\") {
				c = getch();
				if(c === "n") {
					c = "\n";
				} else if(c === "t") {
					c = "\t";
				}
			}
			push(str, c);
			c = getch();
		}
		c = getch();
		val= join(str, "");
		str= ["(string)"];
	} else if(is_num(c)) {
		while(is_num(c)) {
			push(str, c);
			c = getch();
		}
		val = join(str, "");
		str = ["(number)"];
	} else if(is_alphanum(c)) {
		while(is_alphanum(c)) {
			push(str, c);
			c = getch();
		}
	} else if(is_symb(c)) {
		while(is_symb(c)) {
			push(str, c);
			c = getch();
		}
	} else if(c === "/") {
		c = getch();
		if(c === "/") {
			while(c !== "\n") {
				c = getch();
			}
			token_c = getch();
			return next_token();
		} else {
			push(str, "/");
		}
	} else {
		push(str, c);
		c = getch();
	}

	token_c = c;
	token = {};

	if(str[0] === undefined) {
		str = "(end)";
	} else {
		str = join(str, "");
	};
	token.str = str;
	token.nud = nuds[str] || function() { return {"id": this.str}; };
	if(leds[str] === undefined) {
		token.bp = 0;
	} else {
		token.led = leds[str].fn;
		token.bp = leds[str].bp;
	};
	token.sep = seps[str];
	token.val = val;
	return token;
};

//
// tables of parsing functions and options for a given token string
//
nuds = {};
leds = {};
seps = {};

//
// functions for defining operator precedence and type
//
infix = function(str, bp) {
	leds[str] = {};
	leds[str].bp = bp;
	leds[str].fn = function(left) {
		return {"id": this.str, "args": [left, parsep(this.bp)]};
	};
};

infixr = function(str, bp) {
	leds[str] = {};
	leds[str].bp = bp;
	leds[str].fn = function(left) {
		return {"id": this.str, "args": [left, parsep(this.bp - 1)]};
	};
};

infixl = function(str, bp) {
	leds[str] = {};
	leds[str].bp = bp;
	leds[str].fn = function(left) {
		return {"id": join(["apply ", this.str], ""), "args": readlist([left])};
	};
};


readlist = function(acc) {
	var p = parse();
	while(p.id !== "(end)") {
		if(p.id !== "(sep)") {
			push(acc, p);
		}
		p = parse();
	}
	return acc;
};

end = function(str) {
	seps[str] = true;
	nuds[str] = function() {
		return {"id": "(end)", "val": this.str};
	}
};

seperator = function(str) {
	seps[str] = true;
	nuds[str] = function() {
		return {"id": "(sep)", "val": this.str};
	}
};

list = function(str) {
	nuds[str] = function() {
		return {"id": str, "args": readlist([])};
	}
};

atom = function(str) {
	nuds[str] = function() {
		return {"id": this.str};
	}
};

prefix = function(str) {
	nuds[str] = function() {
		return {"id": this.str, "args": [parse()]};
	}
};

prefix2 = function(str) {
	nuds[str] = function() {
		return {"id": this.str, "args": [parse(), parse()]};
	}
};

nuds["(string)"] = function() {
	return {"id": this.str, "val": this.val};
};

nuds["(number)"] = function() {
	return {"id": this.str, "val": this.val};
};

nuds["var"] = function() {
	var acc = [];
	var p = parse();
	while(p.id !== "(end)" && p.val !== ";") {
		if(p.id !== "(sep)") {
			push(acc, p);
		}
		p = parse();
	}
	return {"id": this.str, "args": acc};
};

//
// Definition of operator precedence and type
//
infix(".", 700);
infixl("(", 600);
infixl("[", 600);
infix("+", 400);
infix("-", 400);
infix("===", 300);
infix("!==", 300);
infix("<=", 300);
infix("<", 300);
infixr("&&", 200);
infixr("||", 200);
infixr("else", 200);
infix("=", 100);
end("]");
end(")");
end("}");
end(undefined);
seperator(":");
seperator(";");
seperator(",");
list("(");
list("{");
list("[");
prefix("return");
prefix("-");
prefix("!");
prefix2("function");
prefix2("if");
prefix2("while");
atom("undefined");
atom("true");
atom("false");

//
// The core parser
//
var parse = function() {
	return parsep(0);
};

token = next_token();
var parsep = function(rbp) {
	var t = token;
	token = next_token();
	var left = t.nud();
	while(rbp < token.bp && !t.sep) {
		t = token;
		token = next_token();
		left = t.led(left);
	}
	return left
};


//
// Compiler
//
ops = {
	"(jump)": 0,
	"(jump if false)": 1,
	"(get local)": 2,
	"(set local)": 3,
	"(set global)": 4,
	"(get global)": 5,
	"(call function)": 6,
	"(call method)": 7,
	//"(json to function)": 8,
	"===" : 9,
	"!==" : 10,
	"<" : 11,
	"<=" : 12,
	"!" : 13,
	"+" : 14,
	// not handling unary minus yet
	 "(neg)": 15,
	 "(minus)": 16,
	//"-" : 16, 
	"!" : 17,
	"(pushnil)" : 18,
	"true" : 19,
	"false" : 20,
	"next" : 21,
	"iterator" : 22,
	"length" : 23,
	"join" : 24,
	"pop" : 25,
	"push" : 26,
	"getch" : 27,
	"is_a" : 28,
	"println" : 29,
	"str2int" : 30,
	"(put)" : 31,
	"(get)" : 32,
	"(new array)" : 33,
	"(new object)" : 34,
	"(get literal)" : 35,
	"this" : 36,
	"(set this)" : 37,
	"map" : 38,
};

local = {}

simplify = function(elem) {
	var i, t;
	var id = elem.id;
	var args = elem.args;
	if (id === "=") {
		map(simplify, args);
		if(args[0].id === "(get)") {
			elem.id = "(put)";
		} else {
			if(local[args[0].val] === true) {
				elem.id = "[set local]";
			} else {
				elem.id = "[set global]";
			}
			args[0].id = "(string)";
		}
	} else if (id === "||") {
		elem.id = "[or]"
		map(simplify, args);
	} else if (id === ".") {
		elem.id = "(get)";
		args[1].val = args[1].id;
		args[1].id = "(string)";
		map(simplify, args);
	} else if (id === "-") {
		if(args.length === 1) {
			elem.id = "(neg)";
		} else {
			elem.id = "(minus)";
		}
	} else if (id === "(") {
		if(args.length !== 1) {
			println("Error: multiparen");
		} else {
			copyobj(args[0], elem);
		}
		simplify(elem);
	} else if (id === "[") {
		elem.id = "[array literal]";
		map(simplify, args);
	} else if (id === "{") {
		elem.id = "[object literal]";
		map(simplify, args);
	} else if (id === "&&") {
		elem.id = "[and]";
		map(simplify, args);
	} else if (id === "apply (") {
		elem.id = "[call]";
		if(ops[args[0].id] !== undefined) {
			elem.id = args[0].id;
			args[0].id = "[noop]";
			return simplify(elem);
		} 
		map(simplify, args);
		if(args[0].id === "(get)") {
			elem.method = true;
		} 
	} else if (id === "apply [") {
		elem.id = "(get)";
		map(simplify, args);
	} else if (id === "[arg-list]") {
	} else if (id === "[block]") {
		map(simplify, args);
	} else if (id === "undefined") {
		elem.id = "(pushnil)";
	} else if (id === "else") {
		println("Error: unexpected-else");
		println(elem);
		println("\n\n\n");
	} else if (id === "function") {
		var l = local;
		local = {};
		var iter = iterator(args[0].args);
		var t = next(iter); 
		while(t !== undefined) {
			local[t.id] = true;
			t = next(iter);
		}
		args[0].id = "[arg-list]";
		args[1].id = "[block]";
		map(simplify, args);
		elem.locals = local;
		local = l;
	} else if (id === "if") {
		if(args[1].id === "else") {
			elem.has_else = true;
			args[2] = args[1].args[1];
			args[1] = args[1].args[0];
			if(args[2].id === "{") {
				args[2].id = "[block]";
			}
		}
		if(args[1].id === "{") {
			args[1].id = "[block]";
		}
		map(simplify, args);
	} else if (id === "[noop]") {
	} else if (id === "(number)") {
	} else if (id === "return") {
		map(simplify, args);
	} else if (id === "(string)") {
	} else if (id === "var") {
		t = [];
		i = 0;
		while(i<args.length) {
			if(args[i].id === "=") {
				push(t, args[i]);
				local[args[i].args[0].id] = true;
			} else {
				local[args[i].id] = true;
			}
			i = i + 1;
		}
		elem.id = "[block]";
		elem.args = t;
		map(simplify, elem.args);
	} else if (id === "while") {
		args[1].id = "[block]";
		map(simplify, args);
	} else {
		if(ops[id]) {
			map(simplify, args);
		} else if(local[id]) {
			elem.val = id;
			elem.id = "[get local]";
		} else {
			elem.val = id;
			elem.id = "[get global]";
		}
	}
	return elem;
}

// const pool, and stack positions

nummap = {
	"ids": {},
	"nextid": 0,
	"get": function(val) {
		var id = this.ids[val];
		if(id === undefined) {
			id = this.nextid;
			this.nextid = id + 1;
			this.ids[val] = id;
		}
		return [id, val];
	}
}

locals = copyobj(nummap, {});
literals = copyobj(nummap, {});

functions = [];


compile = function(withresult, elem, acc) {
	var id = elem.id;
	var args = elem.args;
	var i;
	if(ops[id] !== undefined) {
		i = 0;
		if(args !== undefined) {
			while(i<args.length) {
				compile(withresult, args[i],acc);
				i = i + 1;
			}
		}
		push(acc, id);
	} else if(id === "[set global]") {
		compile(true, args[1], acc);
		push(acc, "(set global)");
		push(acc, literals.get(args[0].val));
		if(withresult) {
			push(acc, "undefined");
		}
		withresult = true;
	} else if(id === "[get global]") {
		push(acc, "(get global)");
		push(acc, literals.get(elem.val));
	} else if(id === "(string)") {
		push(acc, "(get literal)");
		push(acc, literals.get(elem.val));
	} else if(id === "[call]") {
		i = 1;
		while(i<args.length) {
			compile(true, args[i], acc);
			i = i + 1;
		}
		if(elem.method) {
			println("Method call not implemented yet");
		} else {
			compile(true, args[0],acc);
			push(acc, "(function call)");
			push(acc, args.length - 1);
		}
	} else if(id === "while") {
		var startpos = acc.length - 1;
		compile(true, args[0], acc);
		push(acc, "condjumpfalse");
		var jumpadr_pos = acc.length;
		push(acc, 0);
		compile(false, elem.args[1], acc);
		push(acc, "jump");
		push(acc, startpos - acc.length);
		acc[jumpadr_pos] = acc.length - jumpadr_pos - 1;
		if (withresult) { push(acc, "(pushnil)"); }; withresult = true;
	} else if(id === "[block]") {
		i = 0;
		if(args !== undefined) {
			while(i<args.length) {
				compile(false, args[i],acc);
				i = i + 1;
			}
		}
		if (withresult) { push(acc, "(pushnil)"); }; withresult = true;
	} else if(id === "[noop]") {
		if (withresult) { push(acc, "(pushnil)"); }; withresult = true;
	} else if(id === "[array literal]") {
		push(acc, "(new array)");
		i = 0;
		while(i<args.length) {
			compile(true, args[i],acc);
			push(acc, "push");
			i = i + 1;
		}
	} else if(id === "[object literal]") {
		push(acc, "(new object)");
		i = 0;
		while(i<args.length) {
			compile(true, args[i],acc);
			compile(true, args[i+1],acc);
			push(acc, "(put)");
			i = i + 2;
		}
	} else if(id === "") {
	} else {
		println(join(["\nUnknown node:", "\n"], id));
	};
	if ((!withresult)) {
		push(acc, "pop");
	};
	return acc;
}


t = iterator(readlist([]));
x = next(t);
while(x !== undefined) {
	println(simplify(x));
	println(compile(false, x, []));
	println("\n\n");
	x = next(t);
};