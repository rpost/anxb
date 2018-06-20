package rpost.anxb;

import com.sun.codemodel.*;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.*;
import org.xml.sax.ErrorHandler;
import rpost.anxb.parser.Annotatable;
import rpost.anxb.parser.AnnotationParser;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnotatePlugin extends Plugin {

    private static final QName ANNOTATE = new QName("http://rpost.anxb/annotate", "annotate");

    @Override
    public String getOptionName() {
        return "Xanxb-annotate";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        return 0;
    }

    @Override
    public String getUsage() {
        return "  -Xanxb-annotate    :  add annotations to generated code";
    }

    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
        for (ClassOutline classOutline : model.getClasses()) {
            for (CPluginCustomization classCustomization : getCustomizations(classOutline.target.getCustomizations())) {
                customizeClass(model, classCustomization, classOutline.ref);
            }

            for (FieldOutline fieldOutline : classOutline.getDeclaredFields()) {
                for (CPluginCustomization fieldCustomization : getCustomizations(fieldOutline.getPropertyInfo().getCustomizations())) {
                    customizeField(model, fieldCustomization, classOutline.ref.fields().get(fieldOutline.getPropertyInfo().getName(false)));
                }
            }
        }
        for (EnumOutline enumOutline : model.getEnums()) {
            for (CPluginCustomization enumCustomization : getCustomizations(enumOutline.target.getCustomizations())) {
                customizeClass(model, enumCustomization, enumOutline.clazz);
            }
            for (EnumConstantOutline enumConstantOutline : enumOutline.constants) {
                for (CPluginCustomization enumConstantCustomization : getCustomizations(enumConstantOutline.target.getCustomizations())) {
                    customizeEnumConstant(model, enumConstantCustomization, enumConstantOutline.constRef);
                }
            }
        }
        return true;
    }

    private List<CPluginCustomization> getCustomizations(CCustomizations customizations) {
        ArrayList<CPluginCustomization> result = new ArrayList<CPluginCustomization>();
        for (CPluginCustomization customization : customizations) {
            if (ANNOTATE.getNamespaceURI().equals(customization.element.getNamespaceURI()) &&
                ANNOTATE.getLocalPart().equals(customization.element.getLocalName())) {

                result.add(customization);
            }
        }
        return result;
    }

    private String getTextContent(CPluginCustomization fieldCustomization) {
        return fieldCustomization.element.getTextContent();
    }

    private void customizeField(final Outline model, CPluginCustomization fieldCustomization, final JFieldVar jFieldVar) {
        AnnotationParser.parse(
            getTextContent(fieldCustomization),
            new Annotatable() {
                @Override
                public JAnnotationUse addAnnotation(String fqn) {
                    return jFieldVar.annotate(model.getCodeModel().ref(fqn));
                }
            }
        );
        fieldCustomization.markAsAcknowledged();
    }

    private void customizeClass(final Outline model, CPluginCustomization classCustomization, final JDefinedClass ref) {
        AnnotationParser.parse(
            getTextContent(classCustomization),
            new Annotatable() {
                @Override
                public JAnnotationUse addAnnotation(String fqn) {
                    return ref.annotate(model.getCodeModel().ref(fqn));
                }
            }
        );
        classCustomization.markAsAcknowledged();
    }

    private void customizeEnumConstant(final Outline model, CPluginCustomization enumConstantCustomization, final JEnumConstant constRef) {
        AnnotationParser.parse(
            getTextContent(enumConstantCustomization),
            new Annotatable() {
                @Override
                public JAnnotationUse addAnnotation(String fqn) {
                    return constRef.annotate(model.getCodeModel().ref(fqn));
                }
            }
        );
        enumConstantCustomization.markAsAcknowledged();
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return ANNOTATE.getNamespaceURI().equals(nsUri) && ANNOTATE.getLocalPart().equals(localName);
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Collections.singletonList(ANNOTATE.getNamespaceURI());
    }
}
