package rpost.anxb;

import com.sun.codemodel.*;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.*;
import org.xml.sax.ErrorHandler;

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
        return true;
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
