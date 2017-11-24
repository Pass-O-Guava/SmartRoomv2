package util;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.BindingEnvironment;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.Util;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;


public class StringToInt extends BaseBuiltin {
    /**
    * Return a name for this builtin, normally this will be the name of the 
    * functor that will be used to invoke it.
    */
    public String getName() {
        return "stringToInt";
    }

    /**
    * Return the expected number of arguments for this functor or 0 if the number is flexible.
    */
    public int getArgLength() {
        return 2;
    }

    /**
    * This method is invoked when the builtin is called in a rule body.
    * @param args the array of argument values for the builtin, this is an array 
    * of Nodes, some of which may be Node_RuleVariables.
    * @param length the length of the argument list, may be less than the length of the args array
    * for some rule engines
    * @param context an execution context giving access to other relevant data
    * @return return true if the buildin predicate is deemed to have succeeded in
    * the current environment
    */
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node n1 = getArg(0, args, context);
        if (n1.isLiteral()) {
            Object v1 = n1.getLiteralValue();
            Node stringToInt = null;
            
            if (v1 instanceof String ) {
                String nv1 = (String) v1;
                int number =Integer.parseInt(nv1);
            	//System.out.println("test"+number);
                stringToInt = Util.makeIntNode(number);
                return env.bind(args[1], stringToInt);
            }
        }
        // Doesn't (yet) handle partially bound cases
        return false;
    }
}