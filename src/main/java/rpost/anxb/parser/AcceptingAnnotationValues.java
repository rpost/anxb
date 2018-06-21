package rpost.anxb.parser;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;

public interface AcceptingAnnotationValues extends Annotatable {

    void set(JExpression val);

    JAnnotationArrayMember array();

    JClass referenceClass(String fqn);
}
