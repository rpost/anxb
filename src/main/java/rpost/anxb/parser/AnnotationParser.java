package rpost.anxb.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.GenericVisitorWithDefaults;
import com.sun.codemodel.*;
import rpost.anxb.Reflections;

public class AnnotationParser {

    public static void parse(String input, Annotatable annotatable) {
        AnnotationExpr annotationExpr = JavaParser.parseAnnotation(input);
        annotationExpr.accept(new AnnotationVisitor(), annotatable);
    }

    private static class AnnotationVisitor extends GenericVisitorWithDefaults<Void, Annotatable> {

        @Override
        public Void visit(MarkerAnnotationExpr n, Annotatable annotatable) {
            annotatable.addAnnotation(n.getNameAsString());
            return null;
        }

        @Override
        public Void visit(NormalAnnotationExpr n, Annotatable annotatable) {
            JAnnotationUse annotation = annotatable.addAnnotation(n.getNameAsString());
            ExpressionVisitor expressionVisitor = new ExpressionVisitor();
            for (MemberValuePair memberValuePair : n.getPairs()) {
                String name = memberValuePair.getName().getIdentifier();
                Expression expression = memberValuePair.getValue();
                expressionVisitor.visit(expression, new JAnnotationUseFieldAdapter(annotation, name));
            }
            return null;
        }

        @Override
        public Void visit(SingleMemberAnnotationExpr n, Annotatable annotatable) {
            JAnnotationUse annotation = annotatable.addAnnotation(n.getNameAsString());
            new ExpressionVisitor().visit(n.getMemberValue(), new JAnnotationUseFieldAdapter(annotation, "value"));
            return null;
        }
    }

    private static class ExpressionVisitor extends GenericVisitorWithDefaults<Void, AceptingAnnotationValues> {

        public Void visit(Expression expression, AceptingAnnotationValues acceptingAnnotationValues) {
            if (expression instanceof ArrayInitializerExpr) {
                return visit(((ArrayInitializerExpr) expression), acceptingAnnotationValues);
            }
            if (expression instanceof BooleanLiteralExpr) {
                return visit(((BooleanLiteralExpr) expression), acceptingAnnotationValues);
            }
            if (expression instanceof ClassExpr) {
                return visit(((ClassExpr) expression), acceptingAnnotationValues);
            }
            if (expression instanceof DoubleLiteralExpr) {
                return visit((DoubleLiteralExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof LongLiteralExpr) {
                return visit((LongLiteralExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof NullLiteralExpr) {
                return visit((NullLiteralExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof StringLiteralExpr) {
                return visit((StringLiteralExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof CharLiteralExpr) {
                return visit((CharLiteralExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof IntegerLiteralExpr) {
                return visit((IntegerLiteralExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof FieldAccessExpr) {
                return visit((FieldAccessExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof CastExpr) {
                return visit((CastExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof SingleMemberAnnotationExpr) {
                return visit((SingleMemberAnnotationExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof NormalAnnotationExpr) {
                return visit((NormalAnnotationExpr) expression, acceptingAnnotationValues);
            }
            if (expression instanceof MarkerAnnotationExpr) {
                return visit((MarkerAnnotationExpr) expression, acceptingAnnotationValues);
            }
            throw new IllegalStateException("Expression not supported " + expression.getClass());
        }

        @Override
        public Void visit(ArrayInitializerExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            JAnnotationArrayMember array = acceptingAnnotationValues.array();
            ExpressionVisitor expressionVisitor = new ExpressionVisitor();
            for (Expression expression : n.getValues()) {
                expressionVisitor.visit(expression, new JAnnotationArrayMemberAdapter(array));
            }
            return null;
        }

        @Override
        public Void visit(BooleanLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.lit(n.getValue()));
            return null;
        }

        @Override
        public Void visit(ClassExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.dotclass(acceptingAnnotationValues.referenceClass(n.getTypeAsString())));
            return null;
        }

        @Override
        public Void visit(DoubleLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.lit(n.asDouble()));
            return null;
        }

        @Override
        public Void visit(LongLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.lit(n.asLong()));
            return null;
        }

        @Override
        public Void visit(IntegerLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.lit(n.asInt()));
            return null;
        }

        @Override
        public Void visit(MarkerAnnotationExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            new AnnotationVisitor().visit(n, acceptingAnnotationValues);
            return null;
        }

        @Override
        public Void visit(NormalAnnotationExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            new AnnotationVisitor().visit(n, acceptingAnnotationValues);
            return null;
        }

        @Override
        public Void visit(SingleMemberAnnotationExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            new AnnotationVisitor().visit(n, acceptingAnnotationValues);
            return null;
        }

        @Override
        public Void visit(NullLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr._null());
            return null;
        }

        @Override
        public Void visit(StringLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.lit(n.asString()));
            return null;
        }

        @Override
        public Void visit(CharLiteralExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            acceptingAnnotationValues.set(JExpr.lit(n.asChar()));
            return null;
        }

        @Override
        public Void visit(FieldAccessExpr n, AceptingAnnotationValues acceptingAnnotationValues) {
            JFieldRef jFieldRef = Reflections.construct(
                JFieldRef.class,
                new Class[]{JType.class, String.class},
                new Object[]{acceptingAnnotationValues.referenceClass(n.getScope().toString()), n.getName().getIdentifier()}
            );
            acceptingAnnotationValues.set(jFieldRef);
            return null;
        }

        @Override
        public Void visit(final CastExpr n, final AceptingAnnotationValues values) {
            new ExpressionVisitor().visit(
                n.getExpression(),
                new AcceptiongExpression() {
                    @Override
                    public void set(JExpression val) {
                        values.set(JExpr.cast(values.referenceClass(n.getTypeAsString()), val));
                    }
                }
            );
            return null;
        }
    }

    private static abstract class AcceptiongExpression implements AceptingAnnotationValues {
        @Override
        public JAnnotationArrayMember array() {
            throw new IllegalStateException();
        }

        @Override
        public JAnnotationUse addAnnotation(String fqn) {
            throw new IllegalStateException();
        }

        @Override
        public JClass referenceClass(String fqn) {
            throw new IllegalStateException();
        }
    }


    private static class JAnnotationUseFieldAdapter implements AceptingAnnotationValues {

        private final JAnnotationUse jAnnotationUse;
        private final String fieldName;

        public JAnnotationUseFieldAdapter(JAnnotationUse jAnnotationUse, String fieldName) {
            this.jAnnotationUse = jAnnotationUse;
            this.fieldName = fieldName;
        }

        @Override
        public void set(JExpression val) {
            jAnnotationUse.param(fieldName, val);
        }

        @Override
        public JAnnotationUse addAnnotation(String fqn) {
            JCodeModel owner = getCodeModel();
            JAnnotationUse annotationValue = Reflections.construct(
                JAnnotationUse.class,
                new Class[]{JClass.class},
                new Object[]{owner.ref(fqn)}
            );
            Reflections.invokeMethod(
                jAnnotationUse,
                "addValue",
                new Class[]{
                    String.class,
                    JAnnotationValue.class
                },
                new Object[]{
                    fieldName,
                    annotationValue
                }
            );
            return annotationValue;
        }

        @Override
        public JAnnotationArrayMember array() {
            return jAnnotationUse.paramArray(fieldName);
        }

        @Override
        public JClass referenceClass(String fqn) {
            return getCodeModel().ref(fqn);
        }

        private JCodeModel getCodeModel() {
            return Reflections.invokeMethod(jAnnotationUse, "owner", new Class[]{}, new Object[]{});
        }

    }

    private static class JAnnotationArrayMemberAdapter implements AceptingAnnotationValues {

        private JAnnotationArrayMember jAnnotationArrayMember;

        public JAnnotationArrayMemberAdapter(JAnnotationArrayMember jAnnotationArrayMember) {
            this.jAnnotationArrayMember = jAnnotationArrayMember;
        }

        @Override
        public void set(JExpression val) {
            jAnnotationArrayMember.param(val);
        }

        @Override
        public JAnnotationUse addAnnotation(String fqn) {
            JCodeModel owner = getCodeModel();
            JAnnotationUse annotation = Reflections.construct(
                JAnnotationUse.class,
                new Class[]{JClass.class},
                new Object[]{owner.ref(fqn)}
            );
            jAnnotationArrayMember.param(annotation);
            return annotation;
        }

        @Override
        public JAnnotationArrayMember array() {
            throw new IllegalStateException("Forbidden by JLS!");
        }

        @Override
        public JClass referenceClass(String fqn) {
            return getCodeModel().ref(fqn);
        }

        private JCodeModel getCodeModel() {
            return Reflections.getFieldValue(jAnnotationArrayMember, "owner");
        }

    }
}
