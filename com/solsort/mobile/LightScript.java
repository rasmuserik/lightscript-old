//<editor-fold desc="license and imports">
package com.solsort.mobile;

/**
This software is released under the 
AFFERO GENERAL PUBLIC LICENSE version 3

(the actual license text can be retrieved
from the Free Software Foundation:
http://www.gnu.org/licenses/agpl-3.0.html)

Copyright, 2009-2010, Rasmus Jensen, rasmus@solsort.com

Contact for other licensing options.
 */
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Stack;

/*`\subsection{Variables}'*/
/** Instances of the LightScript object, is an execution context,
 * where code can be parsed, compiled, and executed.
 *
 * @author Rasmus Jensen, rasmus@lightscript.net
 * @version 1.2
 */
// </editor-fold>

class OpCodes {
    /* Identifiers, used both as node type,
     * and also used as opcode.
     */
    public static final int NONE = 127;
    public static final int TRUE = 0;
    public static final int FALSE = 1;
    public static final int UNDEFINED = 2;
    public static final int NULL = 3;
    public static final int PAREN = 4;
    public static final int LIST_LITERAL = 5;
    public static final int CURLY = 6;
    public static final int VAR = 7;
    public static final int BUILD_FUNCTION = 8;
    public static final int IF = 9;
    public static final int WHILE = 10;
    public static final int CALL_FUNCTION = 11;
    public static final int AND = 12;
    public static final int OR = 13;
    public static final int ELSE = 14;
    public static final int SET = 15;
    public static final int IDENT = 16;
    public static final int BLOCK = 17;
    public static final int SEP = 18;
    public static final int IN = 19;
    public static final int FOR = 20;
    public static final int END = 21;
    public static final int CATCH = 22;
    public static final int DO = 23;
    public static final int INC = 24;
    public static final int DEC = 25;
    public static final int ADD = 26;
    public static final int EQUALS = 27;
    public static final int LESS = 29;
    public static final int LESS_EQUALS = 30;
    public static final int LITERAL = 31;
    public static final int MUL = 32;
    public static final int NEG = 33;
    public static final int NOT = 34;
    public static final int NOT_EQUALS = 35;
    public static final int REM = 37;
    public static final int RETURN = 38;
    public static final int SHIFT_RIGHT_ARITHMETIC = 39;
    public static final int SUB = 40;
    public static final int SUBSCRIPT = 41;
    public static final int THIS = 42;
    public static final int THROW = 43;
    public static final int TRY = 44;
    public static final int UNTRY = 45;
    public static final int BOX_IT = 46;
    public static final int BUILD_FN = 47;
    public static final int CALL_FN = 48;
    public static final int DROP = 49;
    public static final int DUP = 50;
    public static final int GET_BOXED = 52;
    public static final int GET_BOXED_CLOSURE = 53;
    public static final int GET_CLOSURE = 54;
    public static final int GET_LOCAL = 55;
    public static final int INC_SP = 56;
    public static final int JUMP = 57;
    public static final int JUMP_IF_FALSE = 58;
    public static final int JUMP_IF_TRUE = 59;
    public static final int NEW_DICT = 60;
    public static final int NEW_LIST = 61;
    public static final int NEXT = 62;
    public static final int POP = 63;
    public static final int PUSH = 64;
    public static final int PUT = 65;
    public static final int SAVE_PC = 66;
    public static final int SET_BOXED = 67;
    public static final int SET_CLOSURE = 68;
    public static final int SET_LOCAL = 69;
    public static final int SET_THIS = 70;
    public static final int SWAP = 71;
    public static final int DIV = 72;
    public static final int NEW_ITER = 73;
    public static final int JUMP_IF_UNDEFINED = 74;
    public static final int DELETE = 75;
    public static final int NEW = 76;
    public static final int GLOBAL = 77;
    public static final int SHIFT_RIGHT = 78;
    public static final int SHIFT_LEFT = 79;
    public static final int BITWISE_OR = 81;
    public static final int BITWISE_XOR = 82;
    public static final int BITWISE_AND = 83;
    public static final int BITWISE_NOT = 84;
}

public final class LightScript {
    // TODO: globals object

    private static Object globals = null;

    public static int toInt(Object object) {
        if (object instanceof Integer) {
            return ((Integer) object).intValue();
        }
        // CLASS DISPATCH

        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*`\subsection{Public functions}'*/
    // <editor-fold desc="public api">
    /** Get the default-setter function */
    public LightScriptFunction defaultSetter() {
        return (LightScriptFunction) executionContext[EC_SETTER];
    }

    /** Set the default-setter function.
     * The default setter function is called as a method on the object,
     * with the key and the value as arguments. */
    public void defaultSetter(LightScriptFunction f) {
        executionContext[EC_SETTER] = f;
    }

    /** Get the default-getter function */
    public LightScriptFunction defaultGetter() {
        return (LightScriptFunction) executionContext[EC_GETTER];
    }

    /** Set the default-getter function.
     * The default getter function is called as a method on the object,
     * with a single argument, which is the key */
    public void defaultGetter(LightScriptFunction f) {
        executionContext[EC_GETTER] = f;
    }
    /**
     * context for execution
     */
    public Object[] executionContext;

    /** Constructor, loading standard library */
    public LightScript() {
        executionContext = new Object[9];
        executionContext[EC_GLOBALS] = new Hashtable();
        StdLib.register(this);
    }

    /** Shorthands for executing a LightScript function */
    public static Object apply(Object thisPtr, LightScriptFunction f) throws LightScriptException {
        Object args[] = {thisPtr};
        return f.apply(args, 0, 0);
    }

    /** Shorthands for executing a LightScript function */
    public static Object apply(Object thisPtr, LightScriptFunction f, Object arg1) throws LightScriptException {
        Object args[] = {thisPtr, arg1};
        return f.apply(args, 0, 1);
    }

    /** Shorthands for executing a LightScript function */
    public static Object apply(Object thisPtr, LightScriptFunction f, Object arg1, Object arg2) throws LightScriptException {
        Object args[] = {thisPtr, arg1, arg2};
        return f.apply(args, 0, 2);
    }

    /** Shorthands for executing a LightScript function */
    public static Object apply(Object thisPtr, LightScriptFunction f, Object arg1, Object arg2, Object arg3) throws LightScriptException {
        Object args[] = {thisPtr, arg1, arg2, arg3};
        return f.apply(args, 0, 3);
    }

    /** Shorthand for evaluating a string that contains LightScript code */
    public Object eval(String s) throws LightScriptException {
        return eval(new ByteArrayInputStream(s.getBytes()));
    }

    /** Parse and execute LightScript code read from an input stream */
    public Object eval(InputStream is) throws LightScriptException {
        Object result, t = UNDEFINED;
        Compiler c = new Compiler(is, executionContext);
        do {
            result = t;
            t = c.evalNext(is);
        } while (t != null);

        return result;
    }

    /** Set a global value for this execution context */
    public void set(Object key, Object value) {
        Object[] box = (Object[]) ((Hashtable) executionContext[EC_GLOBALS]).get(key);
        if (box == null) {
            box = new Object[1];
            ((Hashtable) executionContext[EC_GLOBALS]).put(key, box);
        }
        box[0] = value;
    }

    /** Retrieve a global value from this execution context */
    public Object get(Object key) {
        Object[] box = (Object[]) ((Hashtable) executionContext[EC_GLOBALS]).get(key);
        if (box == null) {
            return null;
        } else {
            return box[0];
        }
    }
    /** The true truth value of results
     * of tests/comparisons within LightScript */
    public static final Object TRUE = new StringBuffer("true");
    /** The null value within LightScript */
    public static final Object NULL = new StringBuffer("null");
    /** The undefined value within LightScript */
    public static final Object UNDEFINED = new StringBuffer("undefined");
    /** The false truth value of results
     * of tests/comparisons within LightScript */
    public static final Object FALSE = new StringBuffer("false");
    // </editor-fold>
    // <editor-fold desc="options">
    /*`\section{Definitions, API, and utility functions}'*/

    /* If debugging is enabled, more tests are run during run-time,
     * and errors may be caught in a more readable way.
     * It also adds support for more readable printing of
     * id, etc.
     */
    private static final boolean DEBUG_ENABLED = true;
    private static final boolean __PRINT_EXECUTED_INSTRUCTIONS__ = false;
    private static final boolean __DO_YIELD__ = true;

    /* If enabled, wipe the stack on function exit,
     * to kill dangling pointers on execution stack,
     * for better GC at performance price
     */
    private static final boolean __CLEAR_STACK__ = true;
    // </editor-fold>
    // <editor-fold desc="defines">

    /** Sizes of different kinds of stack frames */
    private static final int RET_FRAME_SIZE = 4;
    private static final int TRY_FRAME_SIZE = 5;
    /** Token used for separators (;,:), which are just discarded */
    private static final Object[] SEP_TOKEN = {new Integer(OpCodes.SEP)};
    /** The globals variables in this execution context.
     * they are boxed, in such that they can be passed
     * to the closure of af function, which will then
     * be able to modify it without looking it up here */
    private static final int EC_GLOBALS = 0;
    private static final int EC_OBJECT_PROTOTYPE = 1;
    private static final int EC_ARRAY_PROTOTYPE = 2;
    private static final int EC_FUNCTION_PROTOTYPE = 3;
    private static final int EC_STRING_PROTOTYPE = 4;
    /* Index in executionContext for the default setter function,
     * which is called when a property is set on
     * an object which is neither a Stack, Hashtable nor LightScriptObject
     *
     * The apply method of the setter gets the container as thisPtr,
     * and takes the key and value as arguments
     */
    private static final int EC_SETTER = 5;
    /* Index in executionContext for the default getter function,
     * called when subscripting an object
     * which is not a Stack, Hashtable, String nor LightScriptObject
     * or when the subscripting of any of those objects returns null.
     * (non-integer on stacks/strings, keys not found in Hashtable or
     * its prototypes, when LightScriptObject.get returns null)
     *
     * The apply method of the getter gets the container as thisPtr,
     * and takes the key as argument
     */
    private static final int EC_GETTER = 6;
    private static final int EC_NEW_ITER = 8;
    //</editor-fold>
    /*`\subsection{Debugging}'*/
    //<editor-fold>
    /** Mapping from ID to name of ID */
    private static final String[] idNames = {
        "", "", "", "", "PAREN", "LIST_LITERAL", "CURLY", "VAR",
        "BUILD_FUNCTION", "IF", "WHILE", "CALL_FUNCTION", "AND",
        "OR", "ELSE", "SET", "IDENT", "BLOCK", "SEP", "IN", "FOR",
        "END", "CATCH", "DO", "INC", "DEC", "ADD", "EQUALS",
        "NOT_USED_ANYMORE", "LESS", "LESS_EQUALS", "LITERAL", "MUL", "NEG",
        "NOT", "NOT_EQUALS", "NOT_USED_ANYMORE", "REM", "RETURN", ">>",
        "SUB", "SUBSCRIPT", "THIS", "THROW", "TRY", "UNTRY", "BOX_IT",
        "BUILD_FN", "CALL_FN", "DROP", "DUP", "NOT_USED_ANYMORE",
        "GET_BOXED", "GET_BOXED_CLOSURE", "GET_CLOSURE", "GET_LOCAL",
        "INC_SP", "JUMP", "JUMP_IF_FALSE", "JUMP_IF_TRUE", "NEW_DICT",
        "NEW_LIST", "NEXT", "POP", "PUSH", "PUT", "SAVE_PC",
        "SET_BOXED", "SET_CLOSURE", "SET_LOCAL", "SET_THIS", "SWAP",
        "DIV", "NEW_ITER", "JUMP_IF_UNDEFINED", "DELETE", "NEW", "GLOBAL",
        "SHIFT_RIGHT", "SHIFT_LEFT", "BITWISE_OR", "BITWISE_XOR", "BITWISE_AND",
        "OpCodes.BITWISE_NOT"
    };

    /** Function that maps from ID to a string representation of the ID,
     * robust for integers which is not IDs */
    private static String idName(int id) {
        return "" + id + ((id > 0 && id < idNames.length) ? idNames[id] : "");
    }

    /** A toString, that also works nicely on arrays, and LightScript code */
    private static String stringify(Object o) {
        if (DEBUG_ENABLED) {
            if (o == null) {
                return "null";
            } else if (o instanceof Object[]) {
                StringBuffer sb = new StringBuffer();
                Object[] os = (Object[]) o;
                sb.append("[");
                if (os.length > 0 && os[0] instanceof Integer) {
                    int id = ((Integer) os[0]).intValue();
                    sb.append(idName(id));
                } else if (os.length > 0) {
                    sb.append(os[0]);
                }
                for (int i = 1; i < os.length; i++) {
                    sb.append(" " + stringify(os[i]));
                }
                sb.append("]");
                return sb.toString();
            } else if (o instanceof Code) {
                Code c = (Code) o;
                StringBuffer sb = new StringBuffer();
                sb.append("closure" + c.argc + "{\n\tcode:");
                for (int i = 0; i < c.code.length; i++) {
                    sb.append(" ");
                    sb.append(idName(c.code[i]));
                }
                sb.append("\n\tclosure:");
                for (int i = 0; i < c.closure.length; i++) {
                    sb.append(" " + i + ":");
                    sb.append(stringify(c.closure[i]));
                }
                sb.append("\n\tconstPool:");
                for (int i = 0; i < c.constPool.length; i++) {
                    sb.append(" " + i + ":");
                    sb.append(stringify(c.constPool[i]));
                }
                sb.append("\n}");
                return sb.toString();
            } else {
                return o.toString();
            }
        } else {
            return o.toString();
        }
    }
    //</editor-fold>
    /*`\subsection{Utility functions}'*/
    //<editor-fold>

    /* Constructors for nodes of the Abstract Syntax Tree.
     * Each node is an array containing an ID, followed by 
     * its children or literal values */
    /** (id, o) -> (Object []) {new Integer(id), o} */
    private static Object[] v(int id, Object o) {
        Object[] result = {new Integer(id), o};
        return result;
    }

    /** (id, o1, o2) -> (Object []) {new Integer(id), o1, o2} */
    private static Object[] v(int id, Object o1, Object o2) {
        Object[] result = {new Integer(id), o1, o2};
        return result;
    }

    /** (id, o1, o2, o3) -> (Object []) {new Integer(id), o1, o2, o3} */
    private static Object[] v(int id, Object o1, Object o2, Object o3) {
        Object[] result = {new Integer(id), o1, o2, o3};
        return result;
    }

    /** Returns a new object array, with the seperator tokens removed */
    private static Object[] stripSep(Object[] os) {
        Stack s = new Stack();
        for (int i = 0; i < os.length; i++) {
            if (os[i] != SEP_TOKEN) {
                s.push(os[i]);
            }
        }
        os = new Object[s.size()];
        s.copyInto(os);
        return os;
    }

    /** Push a value into a stack if it is not already there */
    private static void stackAdd(Stack s, Object val) {
        if (s == null) {
            return;
        }
        int pos = s.indexOf(val);
        if (pos == -1) {
            pos = s.size();
            s.push(val);
        }
    }
    //</editor-fold>
    /*`\subsection{Utility classes}'*/
    /*`\subsubsection{StdLib}'*/

    /*`\subsubsection{Code}'*/
    /**
     * Analysis of variables in a function being compiled,
     * updated during the parsing.
     */
    public static class Code implements LightScriptFunction {

        public Object apply(Object[] args, int argpos, int argcount)
                throws LightScriptException {
            if (!DEBUG_ENABLED || argcount == argc) {
                Object stack[];
                if (argpos != 0) {
                    stack = new Object[argcount + 1];
                    for (int i = 0; i <= argcount; i++) {
                        stack[i] = args[argpos + i];
                    }
                } else {
                    stack = args;
                }
                return execute(this, stack, argcount);
            } else {
                throw new LightScriptException("Wrong number of arguments");
            }
        }
        public int argc;
        public byte[] code;
        public Object[] constPool;
        public Object[] closure;
        public int maxDepth;

        public Code(int argc, byte[] code, Object[] constPool, Object[] closure, int maxDepth) {
            this.argc = argc;
            this.code = code;
            this.constPool = constPool;
            this.closure = closure;
            this.maxDepth = maxDepth;
        }

        public Code(Code cl) {
            this.argc = cl.argc;
            this.code = cl.code;
            this.constPool = cl.constPool;
            this.maxDepth = cl.maxDepth;
        }
    }

    public static class Compiler {

        /* The function id for the null denominator functions */
        private static final int NUD_NONE = 13;
        private static final int NUD_IDENT = 1;
        private static final int NUD_LITERAL = 2;
        private static final int NUD_END = 3;
        private static final int NUD_SEP = 4;
        private static final int NUD_LIST = 5;
        private static final int NUD_PREFIX = 6;
        private static final int NUD_PREFIX2 = 7;
        private static final int NUD_FUNCTION = 8;
        private static final int NUD_VAR = 9;
        private static final int NUD_ATOM = 10;
        private static final int NUD_CATCH = 11;
        private static final int NUD_CONST = 12;

        /* The function id for the null denominator functions */
        private static final int LED_NONE = 8;
        private static final int LED_DOT = 1;
        private static final int LED_INFIX = 2;
        private static final int LED_INFIXR = 3;
        private static final int LED_INFIX_LIST = 4;
        private static final int LED_INFIX_IF = 5;
        private static final int LED_OPASSIGN = 6;
        private static final int LED_INFIX_SWAP = 7;

        /* Tokens objects are encoded as integers */
        /** The number of bits per denominator function */
        private static final int SIZE_FN = 4;
        /** The number of bits per id */
        private static final int SIZE_ID = 7;
        //</editor-fold>

        /* Masks for function/id */
        private static final int MASK_ID = ((1 << SIZE_ID) - 1);
        private static final int MASK_FN = ((1 << SIZE_FN) - 1);

        /* Mask for the binding power / priority */
        private static final int MASK_BP = (-1 << (2 * SIZE_ID + 2 * SIZE_FN));
        /** The sep token, encoded as an integer */
        private static final int TOKEN_SEP = ((((((((0 << SIZE_FN)
                | NUD_SEP) << SIZE_ID)
                | OpCodes.NONE) << SIZE_FN)
                | LED_NONE) << SIZE_ID)
                | OpCodes.NONE);
        /** The end token, encoded as an integer */
        private static final int TOKEN_END = ((((((((0 << SIZE_FN)
                | NUD_END) << SIZE_ID)
                | OpCodes.NONE) << SIZE_FN)
                | LED_NONE) << SIZE_ID)
                | OpCodes.NONE);
        /** The token used for literals, encoded as an integer */
        private static final int TOKEN_LITERAL = ((((((((0 << SIZE_FN)
                | NUD_LITERAL) << SIZE_ID)
                | OpCodes.NONE) << SIZE_FN)
                | LED_NONE) << SIZE_ID)
                | OpCodes.NONE);
        /** The token used for identifiers, encoded as an integer */
        private static final int TOKEN_IDENT = ((((((((0 << SIZE_FN)
                | NUD_IDENT) << SIZE_ID)
                | OpCodes.NONE) << SIZE_FN)
                | LED_NONE) << SIZE_ID)
                | OpCodes.NONE);

        /** Token string when reaching end of file, it can only occur
         * at end of file, as it would otherwise be parsed as three
         * tokens: "(", "EOF", and ")". */
        private static final String EOF = "(EOF)";

        /** Evaluate the next statement from an input stream */
        public Object evalNext(InputStream is) throws LightScriptException {
// if we debug, we want the real exception, with line number..
            while (token == TOKEN_SEP) {
                nextToken();
            }
            if (tokenVal == EOF || token == TOKEN_END) {
                return null;
            }
            // parse with every var in closure
            varsUsed = varsLocals = varsBoxed = new Stack();
            Object[] os = parse(0);
            varsClosure = varsUsed;

            // compile
            Code compiledCode = compile(os);
            // create closure from globals
            for (int i = 0; i < compiledCode.closure.length; i++) {
                Object box = ((Hashtable) executionContext[EC_GLOBALS]).get(compiledCode.closure[i]);
                if (box == null) {
                    box = new Object[1];
                    ((Object[]) box)[0] = UNDEFINED;
                    ((Hashtable) executionContext[EC_GLOBALS]).put(compiledCode.closure[i], box);
                }
                compiledCode.closure[i] = box;
            }
            Object stack[] = {globals};
            return execute(compiledCode, stack, 0);
        }

        public Compiler(InputStream is, Object[] executionContext) {
            this.is = is;
            this.executionContext = executionContext;
            this.sb = new StringBuffer();
            this.c = ' ';
            this.varsArgc = 0;
            nextToken();
        }
        private Object executionContext[];
        private static Hashtable idMapping;
        // <editor-fold desc="properties">
        /** This stack is used during compilation to build the constant pool
         * of the compiled function. The constant pool contains the constants
         * that are used during execution of the function */
        private Stack constPool;

        /* Used to keep track of stack depth during compilation, to be able to
         * resolve variables */
        private int maxDepth;
        private int depth;
        /** This stringbuffer is used as a dynamically sized bytevector
         * where opcodes are added, during the compilation */
        private StringBuffer code;
        /** The stream which we are parsing */
        private InputStream is;
        /** the just read character */
        private int c;
        /** the buffer for building the tokens */
        private StringBuffer sb;

        /* Per function statistics
         * Sets of variable names for keeping track of which variables
         * are used where and how, in order to know whether they should
         * be boxed, and be placed on the stack or in closures. */
        /** The variables used within a function */
        private Stack varsUsed;
        /** The variables that needs to be boxed */
        private Stack varsBoxed;
        /** The local variables (arguments, and var-defined) */
        private Stack varsLocals;
        /** The variables in the closure */
        private Stack varsClosure;
        /** The number of arguments to the function, corresponds to the first
         * names in varsLocals */
        private int varsArgc;
        /** The value of the just read token.
         * used if the token is an identifier or literal.
         * possible types are String and Integer */
        private Object tokenVal;
        /** The integer encoded token object, including priority, IDs
         * and Function ids for null/left denominator functions */
        private int token;
        //</editor-fold>

        /*`\section{Tokeniser}'\index{Tokeniser}*/
        // <editor-fold>
        /** Read the next character from the input stream */
        private void nextc() {
            try {
                c = is.read();
            } catch (Exception e) {
                c = -1;
            }
        }

        /** Append a character to the token buffer */
        private void pushc() {
            sb.append((char) c);
            nextc();
        }

        /** Test if the current character is a number */
        private boolean isNum() {
            return '0' <= c && c <= '9';
        }

        /** Test if the current character is alphanumeric */
        private boolean isAlphaNum() {
            return isNum() || c == '_' || ('a' <= c && c <= 'z')
                    || c == '$' || ('A' <= c && c <= 'Z');
        }

        /** Test if the current character could be a part of a multi character
         * symbol, such as &&, ||, += and the like. */
        private boolean isSymb() {
            return c == '=' || c == '!' || c == '<' || c == '&' || c == '/' || c == '*'
                    || c == '%' || c == '|' || c == '+' || c == '-' || c == '>';
        }

        /** Read the next toke from the input stream.
         * The token is stored in the token and tokenVal property. */
        private void nextToken() {
            sb.setLength(0);

            // skip whitespaces
            while (c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '/') {
                // comments
                if (c == '/') {
                    nextc();
                    if (c == '/') {
                        while (c != '\n' && c != -1) {
                            nextc();
                        }
                    } else if (c == '*') {
                        for (;;) {
                            nextc();
                            if (c == '*') {
                                nextc();
                                if (c == '/') {
                                    break;
                                }
                            }
                        }
                    } else {
                        resolveToken("/");
                        return;
                    }
                }
                nextc();
            }

            // End of file
            if (c == -1) {
                token = TOKEN_END;
                tokenVal = EOF;
                return;

                // String
            } else if (c == '"' || c == '\'') {
                int quote = c;
                nextc();
                while (c != -1 && c != quote) {
                    if (c == '\\') {
                        nextc();
                        if (c == 'n') {
                            c = '\n';
                        }
                    }
                    pushc();
                }
                nextc();
                token = TOKEN_LITERAL;
                tokenVal = sb.toString();
                return;

                // Number
            } else if (isNum()) {
                do {
                    pushc();
                } while (isNum());
                token = TOKEN_LITERAL;
                tokenVal = Integer.valueOf(sb.toString());
                return;

                // Identifier
            } else if (isAlphaNum()) {
                do {
                    pushc();
                } while (isAlphaNum());

                // Long symbol !== , ===, <= , &&, ...
            } else if (isSymb()) {
                do {
                    pushc();
                } while (isSymb());

                // Single symbol
            } else {
                pushc();
            }
            resolveToken(sb.toString());
            return;
        }
        //</editor-fold>
    /*`\section{Parser}\label{code-lightscript-parser}
        \index{Top down operator precedence parser}'*/
        //<editor-fold>

        /** Parse the next expression from the input stream
         * @param rbp right binding power
         */
        private Object[] parse(int rbp) {
            Object[] left = nud(token);

            // token & MASK_BP extract the binding power/priority of the token
            while (rbp < (token & MASK_BP)) {
                left = led(token, left);
            }

            return left;
        }

        /** Read expressions until an end-token is reached.
         * @param s an accumulator stack where the expressions are appended
         * @return an array of parsed expressions, with s prepended
         */
        private Object[] readList(Stack s) {
            while (token != TOKEN_END) {
                Object[] p = parse(0);
                s.push(p);
            }
            nextToken();

            Object[] result = new Object[s.size()];
            s.copyInto(result);
            return result;
        }

        /** Call the null denominator function for a given token
         * and also read the next token. */
        private Object[] nud(int tok) {
            Object val = tokenVal;
            nextToken();
            int nudId = (tok >> (SIZE_ID + SIZE_FN)) & ((1 << SIZE_ID) - 1);
            // extract the token function id from tok
            switch ((tok >> (SIZE_ID * 2 + SIZE_FN)) & ((1 << SIZE_FN) - 1)) {
                case NUD_IDENT:
                    stackAdd(varsUsed, val);
                    return v(OpCodes.IDENT, val);
                case NUD_LITERAL:
                    return v(OpCodes.LITERAL, val);
                case NUD_CONST:
                    return v(OpCodes.LITERAL,
                            nudId == OpCodes.TRUE ? TRUE
                            : nudId == OpCodes.FALSE ? FALSE
                            : nudId == OpCodes.NULL ? NULL
                            : UNDEFINED);
                case NUD_END:
                    return null;// result does not matter,
                // as this is removed during parsing
                case NUD_SEP:
                    return SEP_TOKEN;
                case NUD_LIST: {
                    Stack s = new Stack();
                    s.push(new Integer(nudId));
                    return readList(s);
                }
                case NUD_ATOM: {
                    Object[] result = {new Integer(nudId)};
                    return result;
                }
                case NUD_PREFIX:
                    return v(nudId, parse(0));
                case NUD_PREFIX2:
                    return v(nudId, parse(0), parse(0));
                case NUD_CATCH: {
                    Object[] o = parse(0);
                    stackAdd(varsLocals, ((Object[]) o[1])[1]);
                    return v(nudId, o, parse(0));
                }
                case NUD_FUNCTION: {
                    // The functio nud is a bit more complex than
                    // the others because variable-use-analysis is done
                    // during the parsing.

                    // save statistics for previous function
                    Stack prevUsed = varsUsed;
                    Stack prevBoxed = varsBoxed;
                    Stack prevLocals = varsLocals;
                    int prevArgc = varsArgc;

                    // create new statistics
                    varsUsed = new Stack();
                    varsBoxed = new Stack();
                    varsLocals = new Stack();

                    // parse arguments
                    Object[] args = stripSep(parse(0));

                    boolean isNamed = false;
                    String fnName = null;
                    // add function arguments to statistics
                    varsArgc = args.length - 1;
                    if (((Integer) args[0]).intValue() == OpCodes.PAREN) {
                        for (int i = 1; i < args.length; i++) {
                            Object[] os = (Object[]) args[i];
                            if (DEBUG_ENABLED) {
                                if (((Integer) os[0]).intValue() != OpCodes.IDENT) {
                                    throw new Error("parameter not variable name"
                                            + stringify(args));
                                }
                            }
                            varsLocals.push(os[1]);
                        }
                    } else {
                        if (DEBUG_ENABLED) {
                            if (((Integer) args[0]).intValue() != OpCodes.CALL_FUNCTION) {
                                throw new Error("parameter not variable name"
                                        + stringify(args));
                            }
                        }
                        varsArgc--;
                        fnName = (String) varsUsed.elementAt(0);
                        varsUsed.removeElementAt(0);
                        isNamed = true;
                        for (int i = 0; i < varsUsed.size(); i++) {
                            varsLocals.push(varsUsed.elementAt(i));
                        }

                    }

                    // parse the body of the function
                    // notice that this may update vars{Used|Boxed|Locals}
                    Object[] body = parse(0);

                    // non-local variables are boxed into the closure
                    for (int i = 0; i < varsUsed.size(); i++) {
                        Object o = varsUsed.elementAt(i);
                        if (!varsLocals.contains(o)) {
                            stackAdd(varsBoxed, o);
                        }
                    }

                    //  find the variables in the closure
                    // and add that they need to be boxed at parent.
                    varsClosure = new Stack();
                    for (int i = 0; i < varsBoxed.size(); i++) {
                        Object o = varsBoxed.elementAt(i);
                        if (!varsLocals.contains(o)) {
                            stackAdd(prevBoxed, o);
                            stackAdd(varsClosure, o);
                        }
                    }
                    Object[] result = v(nudId, compile(body));
                    if (isNamed) {
                        result = v(OpCodes.SET, v(OpCodes.IDENT, fnName), result);
                        stackAdd(prevUsed, fnName);
                    }
                    varsClosure = null;

                    // restore variable statistics
                    // notice that varsClosure is not needed,
                    // as it is calculated before the compile,
                    // and not updated/used other places
                    varsUsed = prevUsed;
                    varsBoxed = prevBoxed;
                    varsLocals = prevLocals;
                    varsArgc = prevArgc;
                    return result;
                }
                case NUD_VAR:
                    Object[] expr = parse(0);
                    int type = ((Integer) expr[0]).intValue();
                    if (type == OpCodes.IDENT) {
                        stackAdd(varsLocals, expr[1]);
                    } else {
                        Object[] expr2 = (Object[]) expr[1];
                        if (DEBUG_ENABLED) {
                            if (type == OpCodes.SET
                                    && ((Integer) expr2[0]).intValue() == OpCodes.IDENT) {
                                stackAdd(varsLocals, expr2[1]);
                            } else {
                                throw new Error("Error in var");
                            }
                        } else {
                            stackAdd(varsLocals, expr2[1]);
                        }
                    }
                    return v(nudId, expr);
                default:
                    if (DEBUG_ENABLED) {
                        throw new Error("Unknown token: " + token + ", val: " + val);
                    } else {
                        return null;
                    }
            }
        }

        /** Call the left denominator function for a given token
         * and also read the next token. */
        private Object[] led(int tok, Object left) {
            nextToken();
            int bp = tok & MASK_BP;
            int ledId = tok & ((1 << SIZE_ID) - 1);
            // extract led function id from token
            switch ((tok >> SIZE_ID) & ((1 << SIZE_FN) - 1)) {
                case LED_INFIX:
                    return v(ledId, left, parse(bp));
                case LED_INFIX_SWAP:
                    return v(ledId, parse(bp), left);
                case LED_OPASSIGN:
                    return v(OpCodes.SET, left, v(ledId, left, parse(bp - 1)));
                case LED_INFIXR:
                    return v(ledId, left, parse(bp - 1));
                case LED_INFIX_LIST: {
                    Stack s = new Stack();
                    s.push(new Integer(ledId));
                    s.push(left);
                    return readList(s);
                }
                case LED_DOT: {
                    Stack t = varsUsed;
                    varsUsed = null;
                    Object[] right = parse(bp);
                    varsUsed = t;
                    if (DEBUG_ENABLED) {
                        if (((Integer) right[0]).intValue() != OpCodes.IDENT) {
                            throw new Error("right side of dot not a string: "
                                    + stringify(right));
                        }
                    }

                    right[0] = new Integer(OpCodes.LITERAL);
                    return v(OpCodes.SUBSCRIPT, left, right);
                }
                case LED_INFIX_IF: {
                    Object branch1 = parse(0);
                    if (DEBUG_ENABLED) {
                        if (parse(0) != SEP_TOKEN) {
                            throw new Error("infix if error");
                        }
                    } else {
                        parse(0);
                    }
                    Object branch2 = parse(0);
                    return v(OpCodes.IF, left, v(OpCodes.ELSE, branch1, branch2));
                }
                default:
                    if (DEBUG_ENABLED) {
                        throw new Error("Unknown led token: " + token);
                    } else {
                        return null;
                    }
            }
        }

        // initialise idMappings
        static {
            /** This string is used for initialising the idMapping.
             * Each token type has five properties:
             * First there is the string of the token,
             *   then ther is the bindingpower,
             *   followed by the null denominator function,
             *     and its corresponding id
             *   and finally the left denominator function,
             *     and its corresponding id
             */
            String identifiers = ""
                    + "(EOF)"
                    + (char) 1
                    + (char) NUD_END
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "]"
                    + (char) 1
                    + (char) NUD_END
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + ")"
                    + (char) 1
                    + (char) NUD_END
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "}"
                    + (char) 1
                    + (char) NUD_END
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "."
                    + (char) 8
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_DOT
                    + (char) OpCodes.SUBSCRIPT
                    + "("
                    + (char) 7
                    + (char) NUD_LIST
                    + (char) OpCodes.PAREN
                    + (char) LED_INFIX_LIST
                    + (char) OpCodes.CALL_FUNCTION
                    + "["
                    + (char) 7
                    + (char) NUD_LIST
                    + (char) OpCodes.LIST_LITERAL
                    + (char) LED_INFIX_LIST
                    + (char) OpCodes.SUBSCRIPT
                    + ">>"
                    + (char) 6
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.SHIFT_RIGHT_ARITHMETIC
                    + "<<"
                    + (char) 6
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.SHIFT_LEFT
                    + "|"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.BITWISE_OR
                    + "^"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.BITWISE_XOR
                    + "&"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.BITWISE_AND
                    + "~"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.BITWISE_NOT
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + ">>>"
                    + (char) 6
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.SHIFT_RIGHT
                    + "/"
                    + (char) 6
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.DIV
                    + "*"
                    + (char) 6
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.MUL
                    + "%"
                    + (char) 6
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.REM
                    + "+"
                    + (char) 5
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.ADD
                    + "-"
                    + (char) 5
                    + (char) NUD_PREFIX
                    + (char) OpCodes.NEG
                    + (char) LED_INFIX
                    + (char) OpCodes.SUB
                    + "=="
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.EQUALS
                    + "==="
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.EQUALS
                    + "!="
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.NOT_EQUALS
                    + "!=="
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.NOT_EQUALS
                    + "<="
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.LESS_EQUALS
                    + "<"
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX
                    + (char) OpCodes.LESS
                    + ">="
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX_SWAP
                    + (char) OpCodes.LESS_EQUALS
                    + ">"
                    + (char) 4
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX_SWAP
                    + (char) OpCodes.LESS
                    + "&&"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIXR
                    + (char) OpCodes.AND
                    + "||"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIXR
                    + (char) OpCodes.OR
                    + "else"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIXR
                    + (char) OpCodes.ELSE
                    + "in"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIXR
                    + (char) OpCodes.IN
                    + "?"
                    + (char) 3
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIX_IF
                    + (char) OpCodes.NONE
                    + "="
                    + (char) 2
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_INFIXR
                    + (char) OpCodes.SET
                    + "+="
                    + (char) 2
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_OPASSIGN
                    + (char) OpCodes.ADD
                    + "-="
                    + (char) 2
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_OPASSIGN
                    + (char) OpCodes.SUB
                    + "*="
                    + (char) 2
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_OPASSIGN
                    + (char) OpCodes.MUL
                    + "/="
                    + (char) 2
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_OPASSIGN
                    + (char) OpCodes.DIV
                    + "%="
                    + (char) 2
                    + (char) NUD_NONE
                    + (char) OpCodes.NONE
                    + (char) LED_OPASSIGN
                    + (char) OpCodes.REM
                    + "++"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.INC
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "--"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.DEC
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + ":"
                    + (char) 1
                    + (char) NUD_SEP
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + ";"
                    + (char) 1
                    + (char) NUD_SEP
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + ","
                    + (char) 1
                    + (char) NUD_SEP
                    + (char) OpCodes.NONE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "{"
                    + (char) 1
                    + (char) NUD_LIST
                    + (char) OpCodes.CURLY
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "var"
                    + (char) 1
                    + (char) NUD_VAR
                    + (char) OpCodes.VAR
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "delete"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.DELETE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "new"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.NEW
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "return"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.RETURN
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "!"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.NOT
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "throw"
                    + (char) 1
                    + (char) NUD_PREFIX
                    + (char) OpCodes.THROW
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "try"
                    + (char) 1
                    + (char) NUD_PREFIX2
                    + (char) OpCodes.TRY
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "catch"
                    + (char) 1
                    + (char) NUD_CATCH
                    + (char) OpCodes.CATCH
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "function"
                    + (char) 1
                    + (char) NUD_FUNCTION
                    + (char) OpCodes.BUILD_FUNCTION
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "do"
                    + (char) 1
                    + (char) NUD_PREFIX2
                    + (char) OpCodes.DO
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "for"
                    + (char) 1
                    + (char) NUD_PREFIX2
                    + (char) OpCodes.FOR
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "if"
                    + (char) 1
                    + (char) NUD_PREFIX2
                    + (char) OpCodes.IF
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "while"
                    + (char) 1
                    + (char) NUD_PREFIX2
                    + (char) OpCodes.WHILE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "undefined"
                    + (char) 1
                    + (char) NUD_CONST
                    + (char) OpCodes.UNDEFINED
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "null"
                    + (char) 1
                    + (char) NUD_CONST
                    + (char) OpCodes.NULL
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "false"
                    + (char) 1
                    + (char) NUD_CONST
                    + (char) OpCodes.FALSE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "this"
                    + (char) 1
                    + (char) NUD_ATOM
                    + (char) OpCodes.THIS
                    + (char) LED_NONE
                    + (char) OpCodes.NONE
                    + "true"
                    + (char) 1
                    + (char) NUD_CONST
                    + (char) OpCodes.TRUE
                    + (char) LED_NONE
                    + (char) OpCodes.NONE;
            idMapping = new Hashtable();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < identifiers.length(); i++) {
                int result = identifiers.charAt(i);
                // result is the binding power
                // next in string is encoded nud/led-function/id object
                if (result < 32) {
                    // read nud function
                    result = (result << SIZE_FN) + identifiers.charAt(++i);
                    // read nud identifier
                    result = (result << SIZE_ID) + identifiers.charAt(++i);
                    // read led function
                    result = (result << SIZE_FN) + identifiers.charAt(++i);
                    // read led identifier
                    result = (result << SIZE_ID) + identifiers.charAt(++i);
                    // save to mapping, and start next string.
                    idMapping.put(sb.toString(), new Integer(result));
                    sb.setLength(0);

                    // result is a char to be addded to the string
                } else {
                    sb.append((char) result);
                }
            }
        }

        private void resolveToken(Object val) {
            tokenVal = val;

            Object o = idMapping.get(val);
            if (o == null) {
                token = TOKEN_IDENT;
            } else {
                token = ((Integer) o).intValue() + MASK_BP;
            }
        }
        //</editor-fold>
    /*`\section{Compiler}'*/
        //<editor-fold>

        private void pushShort(int i) {
            emit(((i >> 8) & 0xff));
            emit((i & 0xff));
        }

        private void setShort(int pos, int i) {
            code.setCharAt(pos - 2, (char) ((i >> 8) & 0xff));
            code.setCharAt(pos - 1, (char) (i & 0xff));
        }

        private void addDepth(int i) {
            depth += i;
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }

        private void assertLength(Object[] list, int len) {
            if (list.length != len) {
                throw new Error("Wrong number of parameters:" + stringify(list));
            }
        }

        private void emit(int opcode) {
            code.append((char) opcode);
        }

        private int constPoolId(Object o) {
            int pos = constPool.indexOf(o);
            if (pos < 0) {
                pos = constPool.size();
                constPool.push(o);
            }
            return pos;
        }

        private static void curlyToBlock(Object oexpr) {
            Object[] expr = (Object[]) oexpr;
            if (((Integer) expr[0]).intValue() == OpCodes.CURLY) {
                expr[0] = new Integer(OpCodes.BLOCK);
            }
        }

        private Code compile(Object[] body) {
            constPool = new Stack();
            constPool.push(executionContext);
            code = new StringBuffer();

            // allocate space for local vars
            maxDepth = depth = varsLocals.size();
            int framesize = depth - varsArgc;
            while (framesize >= 127) {
                emit(OpCodes.INC_SP);
                emit(127);
                framesize -= 127;
            }
            if (framesize > 0) {
                emit(OpCodes.INC_SP);
                emit(framesize);
            }

            // box boxed values in frame
            for (int i = 0; i < varsBoxed.size(); i++) {
                int pos = varsLocals.indexOf(varsBoxed.elementAt(i));
                if (pos != -1) {
                    emit(OpCodes.BOX_IT);
                    pushShort(depth - pos - 1);
                }
            }

            // compile
            curlyToBlock(body);
            compile(body, true);

            // emit return code, including current stack depth to drop
            emit(OpCodes.RETURN);
            pushShort(depth);

            // patch amount of stack space needed
            maxDepth -= varsArgc;

            // create a new code object;
            Code result = new Code(varsArgc, new byte[code.length()],
                    new Object[constPool.size()], new Object[varsClosure.size()], maxDepth);

            // copy values into the code object
            constPool.copyInto(result.constPool);
            varsClosure.copyInto(result.closure);
            for (int i = 0; i < result.code.length; i++) {
                result.code[i] = (byte) code.charAt(i);
            }

            //System.out.println(stringify(body));
            //System.out.println(varsLocals);
            //System.out.println(varsBoxed);
            //System.out.println(varsClosure);
            //System.out.println(result);
            return result;
        }

        private static int childType(Object[] expr, int i) {
            return ((Integer) ((Object[]) expr[i])[0]).intValue();
        }

        /**
         * Generates code that sets the variable to the value of the
         * top of the stack, not altering the stack
         * @param name the name of the variable.
         */
        private void compileSet(Object name) {
            int pos = varsClosure.indexOf(name);
            if (pos >= 0) {
                emit(OpCodes.SET_CLOSURE);
                pushShort(pos);
            } else {
                pos = varsLocals.indexOf(name);
                if (varsBoxed.contains(name)) {
                    emit(OpCodes.SET_BOXED);
                } else {
                    emit(OpCodes.SET_LOCAL);
                }
                pushShort(depth - pos - 1);
            }
        }

        private void compile(Object rawexpr, boolean yieldResult) {
            boolean hasResult;
            Object[] expr = (Object[]) rawexpr;
            int id = ((Integer) expr[0]).intValue();
            switch (id) {
                case OpCodes.ADD:
                case OpCodes.MUL:
                case OpCodes.DIV:
                case OpCodes.SHIFT_RIGHT_ARITHMETIC:
                case OpCodes.SHIFT_RIGHT:
                case OpCodes.SHIFT_LEFT:
                case OpCodes.BITWISE_OR:
                case OpCodes.BITWISE_XOR:
                case OpCodes.BITWISE_AND:
                case OpCodes.REM:
                case OpCodes.SUB:
                case OpCodes.EQUALS:
                case OpCodes.NOT_EQUALS:
                case OpCodes.SUBSCRIPT:
                case OpCodes.LESS_EQUALS:
                case OpCodes.LESS: {
                    compile(expr[1], true);
                    compile(expr[2], true);
                    emit(id);
                    addDepth(-1);
                    hasResult = true;
                    break;
                }
                case OpCodes.BITWISE_NOT:
                case OpCodes.NOT:
                case OpCodes.NEG: {
                    compile(expr[1], true);
                    emit(id);
                    hasResult = true;
                    break;
                }
                case OpCodes.DELETE: {
                    Object[] expr2 = (Object[]) expr[1];
                    int subtype = ((Integer) expr2[0]).intValue();
                    if (subtype == OpCodes.SUBSCRIPT) {
                        compile(expr2[1], true);
                        compile(expr2[2], true);
                    } else if (DEBUG_ENABLED && subtype != OpCodes.IDENT) {
                        throw new Error("Deleting non-var");
                    } else {
                        emit(OpCodes.GLOBAL);
                        addDepth(1);
                        compile(expr2[1], true);
                    }
                    emit(id);
                    addDepth(-1);
                    hasResult = true;
                    break;
                }
                case OpCodes.NEW: {
                    int subtype = childType(expr, 1);
                    if (subtype != OpCodes.CALL_FUNCTION) {
                        expr = v(OpCodes.CALL_FUNCTION, expr);
                    }
                    expr[1] = v(OpCodes.SUBSCRIPT, expr, v(OpCodes.LITERAL, "constructor"));
                    compile(expr, yieldResult);
                    hasResult = yieldResult;
                    break;
                }
                case OpCodes.THIS: {
                    emit(id);
                    addDepth(1);
                    hasResult = true;
                    break;
                }
                case OpCodes.LITERAL: {
                    emit(id);
                    pushShort(constPoolId(expr[1]));
                    hasResult = true;
                    addDepth(1);
                    break;
                }
                case OpCodes.BLOCK: {
                    for (int i = 1; i < expr.length; i++) {
                        compile(expr[i], false);
                    }
                    hasResult = false;
                    break;
                }
                case OpCodes.RETURN: {
                    compile(expr[1], true);
                    emit(OpCodes.RETURN);
                    pushShort(depth);
                    addDepth(-1);
                    hasResult = false;
                    break;
                }
                case OpCodes.IDENT: {
                    String name = (String) expr[1];
                    int pos = varsClosure.indexOf(name);
                    if (pos >= 0) {
                        emit(OpCodes.GET_CLOSURE);
                        pushShort(pos);
                    } else {
                        pos = varsLocals.indexOf(name);
                        if (DEBUG_ENABLED) {
                            if (pos == -1) {
                                throw new Error("Unfound var: " + stringify(expr));
                            }
                        }
                        if (varsBoxed.contains(name)) {
                            emit(OpCodes.GET_BOXED);
                        } else {
                            emit(OpCodes.GET_LOCAL);
                        }
                        pushShort(depth - pos - 1);
                    }
                    addDepth(1);
                    hasResult = true;
                    break;
                }
                case OpCodes.VAR: {
                    int id2 = childType(expr, 1);
                    if (id2 == OpCodes.IDENT) {
                        hasResult = false;
                    } else if (id2 == OpCodes.SET) {
                        compile(expr[1], yieldResult);
                        hasResult = yieldResult;
                    } else {
                        if (DEBUG_ENABLED) {
                            throw new Error("Error in var statement: "
                                    + stringify(expr));
                        } else {
                            return;
                        }
                    }
                    break;
                }
                case OpCodes.SET: {
                    assertLength(expr, 3);
                    int targetType = childType(expr, 1);
                    hasResult = true;
                    if (targetType == OpCodes.IDENT) {
                        String name = (String) ((Object[]) expr[1])[1];
                        compile(expr[2], true);
                        compileSet(name);
                    } else if (targetType == OpCodes.SUBSCRIPT) {
                        Object[] subs = (Object[]) expr[1];
                        compile(subs[1], true);
                        compile(subs[2], true);
                        compile(expr[2], true);
                        emit(OpCodes.PUT);
                        addDepth(-2);
                    } else {
                        if (DEBUG_ENABLED) {
                            throw new Error("Uncompilable assignment operator: "
                                    + stringify(expr));
                        }
                    }
                    break;
                }
                case OpCodes.PAREN: {
                    if (DEBUG_ENABLED) {
                        if (expr.length != 2) {
                            throw new Error("Unexpected content of parenthesis: "
                                    + stringify(expr));
                        }
                    }
                    compile(expr[1], yieldResult);
                    hasResult = yieldResult;
                    break;
                }
                case OpCodes.CALL_FUNCTION: {
                    expr = stripSep(expr);
                    boolean methodcall = (childType(expr, 1) == OpCodes.SUBSCRIPT);

                    // save program counter
                    emit(OpCodes.SAVE_PC);
                    addDepth(RET_FRAME_SIZE);


                    // find the method/function
                    if (methodcall) {
                        Object[] subs = (Object[]) expr[1];
                        compile(subs[1], true);

                        emit(OpCodes.DUP);
                        addDepth(1);

                        compile(subs[2], true);
                        emit(OpCodes.SUBSCRIPT);
                        addDepth(-1);

                        emit(OpCodes.SWAP);

                    } else {
                        compile(expr[1], true);

                        emit(OpCodes.GLOBAL);
                        addDepth(1);
                    }

                    // evaluate parameters
                    for (int i = 2; i < expr.length; i++) {
                        compile(expr[i], true);
                    }

                    // call the function
                    emit(OpCodes.CALL_FN);
                    if (DEBUG_ENABLED) {
                        if (expr.length > 129) {
                            throw new Error("too many parameters");
                        }
                    }
                    emit(expr.length - 2);
                    addDepth(1 - expr.length - RET_FRAME_SIZE);

                    hasResult = true;
                    break;
                }
                case OpCodes.BUILD_FUNCTION: {
                    Object[] vars = ((Code) expr[1]).closure;
                    for (int i = 0; i < vars.length; i++) {
                        String name = (String) vars[i];
                        if (varsClosure.contains(name)) {
                            emit(OpCodes.GET_BOXED_CLOSURE);
                            pushShort(varsClosure.indexOf(name));
                        } else {
                            emit(OpCodes.GET_LOCAL);
                            pushShort(depth - varsLocals.indexOf(name) - 1);
                        }
                        addDepth(1);
                    }
                    emit(OpCodes.LITERAL);
                    pushShort(constPoolId(expr[1]));
                    addDepth(1);
                    emit(OpCodes.BUILD_FN);
                    pushShort(vars.length);
                    addDepth(-vars.length);
                    hasResult = true;
                    break;

                }
                case OpCodes.IF: {
                    int subtype = childType(expr, 2);

                    if (subtype == OpCodes.ELSE) {
                        Object[] branch = (Object[]) expr[2];

                        //    code for condition
                        //    jump_if_true -> label1
                        //    code for branch2
                        //    jump -> label2
                        // label1:
                        //    code for branch1
                        // label2:

                        int pos0, pos1, len;
                        // compile condition
                        compile(expr[1], true);

                        emit(OpCodes.JUMP_IF_TRUE);
                        pushShort(0);
                        pos0 = code.length();
                        addDepth(-1);

                        curlyToBlock(branch[2]);
                        compile(branch[2], yieldResult);

                        emit(OpCodes.JUMP);
                        pushShort(0);
                        pos1 = code.length();
                        len = pos1 - pos0;
                        setShort(pos0, len);

                        addDepth(yieldResult ? -1 : 0);

                        curlyToBlock(branch[1]);
                        compile(branch[1], yieldResult);

                        len = code.length() - pos1;
                        setShort(pos1, len);

                        hasResult = yieldResult;
                        break;
                    } else {
                        int pos0, len;

                        compile(expr[1], true);

                        emit(OpCodes.JUMP_IF_FALSE);
                        pushShort(0);
                        pos0 = code.length();
                        addDepth(-1);

                        curlyToBlock(expr[2]);
                        compile(expr[2], false);

                        len = code.length() - pos0;
                        setShort(pos0, len);

                        hasResult = false;
                        break;
                    }
                }
                case OpCodes.FOR: {
                    Object[] args = (Object[]) expr[1];
                    Object init, cond, step;
                    if (args.length > 2) {
                        //for(..;..;..)
                        int pos = 1;
                        init = args[pos];
                        pos += (init == SEP_TOKEN) ? 1 : 2;
                        cond = args[pos];
                        pos += (cond == SEP_TOKEN) ? 1 : 2;
                        step = (pos < args.length) ? args[pos] : SEP_TOKEN;
                        curlyToBlock(expr[2]);
                        compile(v(OpCodes.BLOCK, init, v(OpCodes.WHILE, cond,
                                v(OpCodes.BLOCK, expr[2], step))), yieldResult);
                        hasResult = yieldResult;
                        break;
                    } else {
                        // for(a in b) c
                        //
                        //   evalute b
                        // labelTop:
                        //   getNextElement
                        //   save -> a
                        //   if no element jump to labelEnd
                        //   c
                        //   jump -> labelTop
                        // labelEnd:
                        //   drop iterator.
                        int pos0, pos1;

                        // find the name
                        Object[] in = (Object[]) ((Object[]) expr[1])[1];
                        Object name = ((Object[]) in[1])[1];
                        if (!(name instanceof String)) {
                            // var name
                            name = ((Object[]) name)[1];
                        }
                        if (DEBUG_ENABLED) {
                            if (!(name instanceof String)) {
                                throw new Error("for-in has no var");
                            }
                        }

                        // evaluate b
                        compile(in[2], true);

                        emit(OpCodes.NEW_ITER);
                        pos0 = code.length();
                        // get next
                        emit(OpCodes.NEXT);
                        addDepth(1);

                        // store value in variable
                        compileSet(name);

                        // exit if done
                        emit(OpCodes.JUMP_IF_UNDEFINED);
                        pushShort(0);
                        pos1 = code.length();
                        addDepth(-1);

                        // compile block
                        curlyToBlock(expr[2]);
                        compile(expr[2], false);

                        emit(OpCodes.JUMP);
                        pushShort(0);

                        setShort(pos1, code.length() - pos1);
                        setShort(code.length(), pos0 - code.length());

                        emit(OpCodes.DROP);
                        addDepth(-1);
                        hasResult = false;

                        break;
                    }

                }
                case OpCodes.SEP: {
                    hasResult = false;
                    break;
                }
                case OpCodes.AND: {
                    assertLength(expr, 3);
                    int pos0, pos1, len;

                    compile(expr[1], true);

                    emit(OpCodes.JUMP_IF_TRUE);
                    pushShort(0);
                    pos0 = code.length();
                    addDepth(-1);

                    compile(v(OpCodes.LITERAL, UNDEFINED), true);

                    emit(OpCodes.JUMP);
                    pushShort(0);
                    pos1 = code.length();
                    len = pos1 - pos0;
                    setShort(pos0, len);
                    addDepth(-1);

                    compile(expr[2], true);
                    len = code.length() - pos1;
                    setShort(pos1, len);
                    hasResult = true;
                    break;
                }
                case OpCodes.OR: {
                    assertLength(expr, 3);
                    int pos0, pos1, len;

                    compile(expr[1], true);

                    emit(OpCodes.DUP);
                    addDepth(1);

                    emit(OpCodes.JUMP_IF_TRUE);
                    pushShort(0);
                    pos0 = code.length();
                    addDepth(-1);

                    emit(OpCodes.DROP);
                    addDepth(-1);

                    compile(expr[2], true);

                    pos1 = code.length();
                    len = pos1 - pos0;
                    setShort(pos0, len);

                    hasResult = true;

                    break;
                }
                case OpCodes.LIST_LITERAL: {
                    emit(OpCodes.NEW_LIST);
                    addDepth(1);

                    for (int i = 1; i < expr.length; i++) {
                        if (childType(expr, i) != OpCodes.SEP) {
                            compile(expr[i], true);
                            emit(OpCodes.PUSH);
                            addDepth(-1);
                        }
                    }
                    hasResult = true;

                    break;
                }
                case OpCodes.CURLY: {
                    emit(OpCodes.NEW_DICT);
                    addDepth(1);

                    int i = 0;
                    while (i < expr.length - 3) {
                        do {
                            ++i;
                        } while (childType(expr, i) == OpCodes.SEP);
                        compile(expr[i], true);
                        do {
                            ++i;
                        } while (childType(expr, i) == OpCodes.SEP);
                        compile(expr[i], true);
                        emit(OpCodes.PUT);
                        addDepth(-2);
                    }
                    hasResult = true;

                    break;
                }
                case OpCodes.THROW: {
                    compile(expr[1], true);
                    emit(OpCodes.THROW);
                    addDepth(-1);
                    hasResult = false;
                    break;
                }
                case OpCodes.TRY: {
                    //   try -> labelHandle;
                    //   ... body
                    //   untry
                    //   jump -> labelExit;
                    // labelHandle:
                    //   set var <- Exception
                    //   handleExpr
                    // labelExit:
                    int pos0, pos1, len;

                    Object[] catchExpr = (Object[]) expr[2];

                    emit(OpCodes.TRY);
                    pushShort(0);
                    pos0 = code.length();
                    addDepth(TRY_FRAME_SIZE);

                    curlyToBlock(expr[1]);
                    compile(expr[1], false);

                    emit(OpCodes.UNTRY);
                    addDepth(-TRY_FRAME_SIZE);

                    emit(OpCodes.JUMP);
                    pushShort(0);
                    pos1 = code.length();

                    // lableHandle:
                    setShort(pos0, code.length() - pos0);

                    addDepth(1);
                    Object name = ((Object[]) ((Object[]) catchExpr[1])[1])[1];

                    compileSet(name);
                    emit(OpCodes.DROP);
                    addDepth(-1);

                    curlyToBlock(catchExpr[2]);
                    compile(catchExpr[2], false);

                    setShort(pos1, code.length() - pos1);

                    hasResult = false;

                    break;

                }
                case OpCodes.WHILE: {
                    // top:
                    //   code for condition
                    //   jump if false -> labelExit
                    //   code for stmt1
                    //   drop
                    //   ...
                    //   code for stmtn
                    //   drop
                    //   jump -> labelTop;
                    // labelExit:

                    int pos0, pos1, len;
                    pos0 = code.length();

                    compile(expr[1], true);
                    emit(OpCodes.JUMP_IF_FALSE);
                    pushShort(0);
                    pos1 = code.length();
                    addDepth(-1);


                    curlyToBlock(expr[2]);
                    compile(expr[2], false);

                    emit(OpCodes.JUMP);
                    pushShort(pos0 - code.length() - 2);

                    setShort(pos1, code.length() - pos1);
                    hasResult = false;
                    break;
                }
                case OpCodes.DO: {
                    int pos0;
                    pos0 = code.length();

                    curlyToBlock(expr[1]);
                    compile(expr[1], false);

                    compile(((Object[]) expr[2])[1], true);
                    emit(OpCodes.JUMP_IF_TRUE);
                    pushShort(pos0 - code.length() - 2);
                    addDepth(-1);
                    hasResult = false;
                    break;
                }
                case OpCodes.DEC: {
                    compile(v(OpCodes.SET, expr[1], v(OpCodes.SUB, expr[1],
                            v(OpCodes.LITERAL, new Integer(1)))), yieldResult);
                    return;
                }
                case OpCodes.INC: {
                    compile(v(OpCodes.SET, expr[1], v(OpCodes.ADD, expr[1],
                            v(OpCodes.LITERAL, new Integer(1)))), yieldResult);
                    return;
                }
                default:
                    if (DEBUG_ENABLED) {
                        throw new Error("Uncompilable expression: " + stringify(expr));
                    } else {
                        return;
                    }
            }

            if (hasResult && !yieldResult) {
                emit(OpCodes.DROP);
                addDepth(-1);
            } else if (yieldResult && !hasResult) {
                compile(v(OpCodes.LITERAL, UNDEFINED), true);
            }
        }
        //</editor-fold>
    }
    /*`\section{Virtual Machine}\index{Virtual machine}'*/
    //<editor-fold>

    private static int readShort(int pc, byte[] code) {
        return (short) (((code[++pc] & 0xff) << 8) | (code[++pc] & 0xff));
    }

    private static boolean toBool(Object o) {
        if (o == TRUE) {
            return true;
        }
        if (o == FALSE || o == NULL || o == UNDEFINED) {
            return false;
        }
        if (o instanceof String) {
            return !((String) o).equals("");
        }
        if (DEBUG_ENABLED) {
            if (o instanceof Integer) {
                return ((Integer) o).intValue() != 0;
            }
            throw new Error("unhandled toBool case for:" + o.toString());
        } else {
            return ((Integer) o).intValue() != 0;
        }
    }

    private static Object[] ensureSpace(Object[] stack, int sp, int maxDepth) {
        if (stack.length <= maxDepth + sp + 1) {
            // Currently keep the allocate stack tight to max, 
            // to catch errors;
            // Possibly change this to grow exponential 
            // for better performance later on.
            Object[] newstack = new Object[maxDepth + sp + 1];
            System.arraycopy(stack, 0, newstack, 0, sp + 1);
            return newstack;
        }
        return stack;
    }

    /**
     * evaluate some bytecode 
     */
    private static Object execute(Code cl, Object[] stack, int argcount) throws LightScriptException {
        //if(!DEBUG_ENABLED) {
        try {
            //}
            int sp = argcount;

            //System.out.println(stringify(cl));
            int pc = -1;
            byte[] code = cl.code;
            Object[] constPool = cl.constPool;
            Object[] closure = cl.closure;
            Object[] executionContext = (Object[]) constPool[0];
            int exceptionHandler = - 1;
            stack = ensureSpace(stack, sp, cl.maxDepth);
            Object thisPtr = stack[0];
            int usedStack;
            if (__CLEAR_STACK__) {
                usedStack = sp + cl.maxDepth;
            }

            for (;;) {
                ++pc;
                if (__PRINT_EXECUTED_INSTRUCTIONS__) {
                    System.out.println("pc:" + pc + " op:" + idName(code[pc])
                            + " sp:" + sp + " stack.length:" + stack.length
                            + " int:" + readShort(pc, code));
                }
                switch (code[pc]) {
                    case OpCodes.INC_SP: {
                        sp += code[++pc];
                        break;
                    }
                    case OpCodes.RETURN: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        Object result = stack[sp];
                        sp -= arg;
                        if (sp == 0) {
                            return result;
                        }
                        if (DEBUG_ENABLED) {
                            if (sp < 0) {
                                throw new Error("Wrong stack discipline"
                                        + sp);
                            }
                        }
                        if (__CLEAR_STACK__) {
                            for (int i = sp; i <= usedStack; i++) {
                                stack[i] = null;
                            }
                        }
                        pc = ((Integer) stack[--sp]).intValue();
                        code = (byte[]) stack[--sp];
                        constPool = (Object[]) stack[--sp];
                        executionContext = (Object[]) constPool[0];
                        closure = (Object[]) stack[--sp];
                        thisPtr = stack[--sp];
                        stack[sp] = result;
                        if (__DO_YIELD__) {
                            Thread.yield();
                        }
                        break;
                    }
                    case OpCodes.SAVE_PC: {
                        stack[++sp] = thisPtr;
                        stack[++sp] = closure;
                        stack[++sp] = constPool;
                        stack[++sp] = code;
                        break;
                    }
                    case OpCodes.CALL_FN: {
                        int argc = code[++pc];
                        Object o = stack[sp - argc - 1];
                        if (o instanceof Code) {
                            Code fn = (Code) o;

                            int deltaSp = fn.argc - argc;
                            stack = ensureSpace(stack, sp, fn.maxDepth + deltaSp);
                            if (__CLEAR_STACK__) {
                                usedStack = sp + fn.maxDepth + deltaSp;
                            }
                            sp += deltaSp;
                            argc = fn.argc;

                            for (int i = 0; i < deltaSp; i++) {
                                stack[sp - i] = UNDEFINED;
                            }

                            stack[sp - argc - 1] = new Integer(pc);
                            thisPtr = stack[sp - argc];
                            pc = -1;
                            code = fn.code;
                            constPool = fn.constPool;
                            executionContext = (Object[]) constPool[0];
                            closure = fn.closure;
                        } else if (o instanceof LightScriptFunction) {
                            try {
                                Object result = ((LightScriptFunction) o).apply(stack, sp - argc, argc);
                                sp -= argc + 1 + RET_FRAME_SIZE;
                                stack[sp] = result;
                            } catch (LightScriptException e) {
                                if (exceptionHandler < 0) {
                                    throw e;
                                } else {
                                    //System.out.println(stringify(stack));
                                    sp = exceptionHandler;
                                    exceptionHandler = ((Integer) stack[sp]).intValue();
                                    pc = ((Integer) stack[--sp]).intValue();
                                    code = (byte[]) stack[--sp];
                                    constPool = (Object[]) stack[--sp];
                                    executionContext = (Object[]) constPool[0];
                                    closure = (Object[]) stack[--sp];
                                    stack[sp] = e.value;
                                }
                                break;
                            }
                        } else {
                            if (DEBUG_ENABLED) {
                                throw new Error("Unknown function:" + o);
                            }
                        }
                        break;
                    }
                    case OpCodes.BUILD_FN: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        Code fn = new Code((Code) stack[sp]);
                        Object[] clos = new Object[arg];
                        for (int i = arg - 1; i >= 0; i--) {
                            --sp;
                            clos[i] = stack[sp];
                        }
                        fn.closure = clos;
                        stack[sp] = fn;
                        break;
                    }
                    case OpCodes.SET_BOXED: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        ((Object[]) stack[sp - arg])[0] = stack[sp];
                        break;
                    }
                    case OpCodes.SET_LOCAL: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        stack[sp - arg] = stack[sp];
                        break;
                    }
                    case OpCodes.SET_CLOSURE: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        ((Object[]) closure[arg])[0] = stack[sp];
                        break;
                    }
                    case OpCodes.GET_BOXED: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        Object o = ((Object[]) stack[sp - arg])[0];
                        stack[++sp] = o;
                        break;
                    }
                    case OpCodes.GET_LOCAL: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        Object o = stack[sp - arg];
                        stack[++sp] = o;
                        break;
                    }
                    case OpCodes.GET_CLOSURE: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        stack[++sp] = ((Object[]) closure[arg])[0];
                        break;
                    }
                    case OpCodes.GET_BOXED_CLOSURE: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        stack[++sp] = closure[arg];
                        break;
                    }
                    case OpCodes.LITERAL: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        stack[++sp] = constPool[arg];
                        break;
                    }
                    case OpCodes.BOX_IT: {
                        int arg = readShort(pc, code);
                        pc += 2;
                        Object[] box = {stack[sp - arg]};
                        stack[sp - arg] = box;
                        break;
                    }
                    case OpCodes.DROP: {
                        --sp;
                        break;
                    }
                    case OpCodes.NOT: {
                        stack[sp] = toBool(stack[sp]) ? FALSE : TRUE;
                        break;
                    }
                    case OpCodes.NEG: {
                        Object o = stack[sp];
                        if (o instanceof Integer) {
                            o = new Integer(-((Integer) o).intValue());
                        } else /* if o is float */ {
// CLASS DISPATCH
                        }
                        stack[sp] = o;
                        break;
                    }
                    case OpCodes.ADD: {
                        Object o2 = stack[sp];
                        --sp;
                        Object o = stack[sp];
                        if (o instanceof Integer && o2 instanceof Integer) {
                            int result = ((Integer) o).intValue();
                            result += ((Integer) o2).intValue();
                            stack[sp] = new Integer(result);
                        } else {
// CLASS DISPATCH
                            stack[sp] = String.valueOf(o) + String.valueOf(o2);
                        }
                        break;
                    }
                    case OpCodes.SUB: {
                        Object o2 = stack[sp];
                        Object o1 = stack[--sp];
                        if (o1 instanceof Integer && o2 instanceof Integer) {
                            stack[sp] = new Integer(((Integer) o1).intValue()
                                    - ((Integer) o2).intValue());
                        } else /* float */ {
// CLASS DISPATCH
                        }
                        break;
                    }
                    case OpCodes.SHIFT_RIGHT_ARITHMETIC: {
                        int result = toInt(stack[sp]);
                        result = toInt(stack[--sp]) >> result;
                        stack[sp] = new Integer(result);
                        break;
                    }
                    case OpCodes.MUL: {
                        Object o2 = stack[sp];
                        Object o1 = stack[--sp];
                        if (o1 instanceof Integer && o2 instanceof Integer) {
                            stack[sp] = new Integer(((Integer) o1).intValue()
                                    * ((Integer) o2).intValue());
                        } else {
// CLASS DISPATCH
                        }
                        break;
                    }
                    case OpCodes.DIV: {
                        Object o2 = stack[sp];
                        Object o1 = stack[--sp];
// CLASS DISPATCH
//                        stack[sp] = fpDiv(toFp(o1), toFp(o2));
                        break;
                    }
                    case OpCodes.REM: {
                        Object o2 = stack[sp];
                        Object o1 = stack[--sp];
                        if (o1 instanceof Integer && o2 instanceof Integer) {
                            stack[sp] = new Integer(((Integer) o1).intValue()
                                    % ((Integer) o2).intValue());
                        } else /* float */ {
// CLASS DISPATCH
                        }
                        break;
                    }
                    case OpCodes.NOT_EQUALS: {
                        Object o = stack[sp];
                        --sp;
                        stack[sp] = (o == null)
                                ? (stack[sp] == null ? FALSE : TRUE)
                                : (o.equals(stack[sp]) ? FALSE : TRUE);
                        break;
                    }
                    case OpCodes.EQUALS: {
                        Object o = stack[sp];
                        --sp;
                        stack[sp] = (o == null)
                                ? (stack[sp] == null ? TRUE : FALSE)
                                : (o.equals(stack[sp]) ? TRUE : FALSE);
                        break;
                    }
                    case OpCodes.PUT: {
                        Object val = stack[sp];
                        Object key = stack[--sp];
                        Object container = stack[--sp];

                        //CLASS DISPATCH replace below
                        if (container instanceof Stack) {
                            int pos = toInt(key);
                            Stack s = (Stack) container;
                            if (pos >= s.size()) {
                                s.setSize(pos + 1);
                            }
                            s.setElementAt(val, pos);

                        } else if (container instanceof Hashtable) {
                            if (val == null) {
                                ((Hashtable) container).remove(key);
                            } else {
                                ((Hashtable) container).put(key, val);
                            }
                        } else {
                            ((LightScriptFunction) executionContext[EC_SETTER]).apply(stack, sp, 2);
                        }
                        break;
                    }
                    case OpCodes.SUBSCRIPT: {
                        Object key = stack[sp];
                        Object container = stack[--sp];
                        Object result = null;


                        // "Object"
                        //CLASS DISPATCH replace below
                        if (container instanceof Hashtable) {
                            result = ((Hashtable) container).get(key);
                            if (result == null) {
                                Object prototype = ((Hashtable) container).get("__proto__");
                                // repeat case OpCodes.SUBSCRIPT with prototype as container
                                if (prototype != null) {
                                    stack[sp] = prototype;
                                    sp += 1;
                                    pc -= 1;
                                    break;
                                }
                            }

                            // "Array"
                        } else if (container instanceof Stack) {
                            if (key instanceof Integer) {
                                int pos = ((Integer) key).intValue();
                                Stack s = (Stack) container;
                                result = 0 <= pos && pos < s.size()
                                        ? s.elementAt(pos)
                                        : null;
                            } else if ("length".equals(key)) {
                                result = new Integer(((Stack) container).size());
                            } else {
                                result = ((Hashtable) executionContext[EC_ARRAY_PROTOTYPE]).get(key);
                            }

                            // "String"
                        } else if (container instanceof String) {
                            if (key instanceof Integer) {
                                int pos = ((Integer) key).intValue();
                                String s = (String) container;
                                result = 0 <= pos && pos < s.length()
                                        ? s.substring(pos, pos + 1)
                                        : null;
                            } else if ("length".equals(key)) {
                                result = new Integer(((String) container).length());
                            } else {
                                result = ((Hashtable) executionContext[EC_STRING_PROTOTYPE]).get(key);
                            }

                            // Other builtin types, by calling userdefined default getter
                        } else {
                            result = ((LightScriptFunction) executionContext[EC_GETTER]).apply(stack, sp, 1);
                        }

                        // prototype property or element within (super-)prototype
                        if (result == null) {
                            if ("__proto__".equals(key)) {
                                if (container instanceof Stack) {
                                    result = (Hashtable) executionContext[EC_ARRAY_PROTOTYPE];
                                } else if (container instanceof String) {
                                    result = (Hashtable) executionContext[EC_STRING_PROTOTYPE];
                                } else {
                                    result = (Hashtable) executionContext[EC_OBJECT_PROTOTYPE];
                                }
                            } else {
                                result = ((Hashtable) executionContext[EC_OBJECT_PROTOTYPE]).get(key);
                                if (result == null) {
                                    result = UNDEFINED;
                                }
                            }
                        }
                        stack[sp] = result;
                        break;
                    }
                    case OpCodes.PUSH: {
                        Object o = stack[sp];
                        ((Stack) stack[--sp]).push(o);
                        break;
                    }
                    case OpCodes.POP: {
                        stack[sp] = ((Stack) stack[sp]).pop();
                        break;
                    }
                    case OpCodes.LESS: {
                        Object o2 = stack[sp];
                        Object o1 = stack[--sp];
                        if (o1 instanceof Integer && o2 instanceof Integer) {
                            stack[sp] = ((Integer) o1).intValue()
                                    < ((Integer) o2).intValue() ? TRUE : FALSE;
                        } else {
// CLASS DISPATCH
                            stack[sp] = o1.toString().compareTo(o2.toString())
                                    < 0 ? TRUE : FALSE;
                        }
                        break;
                    }
                    case OpCodes.LESS_EQUALS: {
                        Object o2 = stack[sp];
                        Object o1 = stack[--sp];
                        if (o1 instanceof Integer && o2 instanceof Integer) {
                            stack[sp] = ((Integer) o1).intValue()
                                    <= ((Integer) o2).intValue() ? TRUE : FALSE;
                        } else {
// CLASS DISPATCH
                            stack[sp] = o1.toString().compareTo(o2.toString())
                                    <= 0 ? TRUE : FALSE;
                        }
                        break;
                    }
                    case OpCodes.JUMP: {
                        pc += readShort(pc, code) + 2;
                        if (__DO_YIELD__) {
                            Thread.yield();
                        }
                        break;
                    }
                    case OpCodes.JUMP_IF_UNDEFINED: {
                        if (UNDEFINED != stack[sp]) {
                            pc += 2;
                        } else {
                            pc += readShort(pc, code) + 2;
                            if (__DO_YIELD__) {
                                Thread.yield();
                            }
                        }
                        --sp;
                        break;
                    }
                    case OpCodes.JUMP_IF_FALSE: {
                        if (toBool(stack[sp])) {
                            pc += 2;
                        } else {
                            pc += readShort(pc, code) + 2;
                            if (__DO_YIELD__) {
                                Thread.yield();
                            }
                        }
                        --sp;
                        break;
                    }
                    case OpCodes.JUMP_IF_TRUE: {
                        if (toBool(stack[sp])) {
                            pc += readShort(pc, code) + 2;
                        } else {
                            pc += 2;
                        }
                        --sp;
                        break;
                    }
                    case OpCodes.DUP: {
                        Object o = stack[sp];
                        stack[++sp] = o;
                        break;
                    }
                    case OpCodes.NEW_LIST: {
                        stack[++sp] = new Stack();
                        break;
                    }
                    case OpCodes.NEW_DICT: {
                        stack[++sp] = new Hashtable();
                        break;
                    }
                    case OpCodes.SET_THIS: {
                        thisPtr = stack[sp];
                        --sp;
                        break;
                    }
                    case OpCodes.THIS: {
                        stack[++sp] = thisPtr;
                        break;
                    }
                    case OpCodes.SWAP: {
                        Object t = stack[sp];
                        stack[sp] = stack[sp - 1];
                        stack[sp - 1] = t;
                        break;
                    }
                    case OpCodes.THROW: {
                        Object result = stack[sp];
                        if (exceptionHandler < 0) {
                            throw new LightScriptException(result);
                        } else {
                            //System.out.println(stringify(stack));
                            sp = exceptionHandler;
                            exceptionHandler = ((Integer) stack[sp]).intValue();
                            pc = ((Integer) stack[--sp]).intValue();
                            code = (byte[]) stack[--sp];
                            constPool = (Object[]) stack[--sp];
                            executionContext = (Object[]) constPool[0];
                            closure = (Object[]) stack[--sp];
                            stack[sp] = result;
                        }
                        break;
                    }
                    case OpCodes.TRY: {
                        stack[++sp] = closure;
                        stack[++sp] = constPool;
                        stack[++sp] = code;
                        stack[++sp] = new Integer(pc + readShort(pc, code) + 2);
                        stack[++sp] = new Integer(exceptionHandler);
                        exceptionHandler = sp;
                        pc += 2;
                        break;
                    }
                    case OpCodes.UNTRY: {
                        exceptionHandler = ((Integer) stack[sp]).intValue();
                        sp -= TRY_FRAME_SIZE;
                        break;
                    }
                    case OpCodes.NEW_ITER: {
                        stack[sp] = ((LightScriptFunction) executionContext[EC_NEW_ITER]).apply(stack, sp, 0);
                        break;
                    }
                    case OpCodes.NEXT: {
                        LightScriptFunction iter = (LightScriptFunction) stack[sp];
                        stack[++sp] = iter.apply(stack, sp, 0);
                        break;
                    }
                    case OpCodes.GLOBAL: {
                        stack[++sp] = globals;
                        break;
                    }
                    case OpCodes.DELETE: {
                        Object key = stack[sp];
                        Object container = stack[--sp];
                        //CLASS DISPATCH replace below
                        if (container instanceof Hashtable) {
                            ((Hashtable) container).remove(key);
                        } else if (container instanceof Stack && key instanceof Integer) {
                            ((Stack) container).setElementAt(UNDEFINED, ((Integer) key).intValue());
                        } else {
                            if (DEBUG_ENABLED) {
                                throw new Error("deleting non-deletable");
                            }
                        }
                        break;
                    }
                    case OpCodes.SHIFT_RIGHT: {
                        int result = toInt(stack[sp]);
                        result = toInt(stack[--sp]) >>> result;
                        stack[sp] = new Integer(result);
                        break;
                    }
                    case OpCodes.SHIFT_LEFT: {
                        int result = toInt(stack[sp]);
                        result = toInt(stack[--sp]) << result;
                        stack[sp] = new Integer(result);
                        break;
                    }
                    case OpCodes.BITWISE_OR: {
                        int result = toInt(stack[sp]);
                        result = toInt(stack[--sp]) | result;
                        stack[sp] = new Integer(result);
                        break;
                    }
                    case OpCodes.BITWISE_XOR: {
                        int result = toInt(stack[sp]);
                        result = toInt(stack[--sp]) ^ result;
                        stack[sp] = new Integer(result);
                        break;
                    }
                    case OpCodes.BITWISE_AND: {
                        int result = toInt(stack[sp]);
                        result = toInt(stack[--sp]) & result;
                        stack[sp] = new Integer(result);
                        break;
                    }
                    case OpCodes.BITWISE_NOT: {
                        int result = ~toInt(stack[sp]);
                        stack[sp] = new Integer(result);
                        break;
                    }
                    default: {
                        if (DEBUG_ENABLED) {
                            throw new Error("Unknown opcode: " + code[pc]);
                        }
                    }
                }
            }
// if we debug, we want the real exception, with line number..
        } catch (Error e) {
            if (!DEBUG_ENABLED) {
                throw new LightScriptException(e);
            } else {
                throw e;
            }
        }
    }
    //</editor-fold>
}

