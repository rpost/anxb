package rpost.anxb.parser;

import com.sun.codemodel.JAnnotationUse;

public interface Annotatable {
    JAnnotationUse addAnnotation(String fqn);
}
