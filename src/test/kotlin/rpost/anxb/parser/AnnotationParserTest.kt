package rpost.anxb.parser

import com.sun.codemodel.JAnnotationUse
import com.sun.codemodel.JClass
import com.sun.codemodel.JCodeModel
import com.sun.codemodel.JFormatter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import rpost.anxb.Reflections
import rpost.anxb.parser.Annotatable
import rpost.anxb.parser.AnnotationParser
import java.io.StringWriter
import java.util.stream.Stream

class AnnotationParserTest {

    @ParameterizedTest
    @MethodSource("data")
    fun testParse(input: String, expectedResult: String) {
        val dummyAnnotatable = DummyAnnotatable()
        AnnotationParser.parse(input, dummyAnnotatable)

        val stringWriter = StringWriter()
        val jFormatter = JFormatter(stringWriter)
        dummyAnnotatable.annotation!!.generate(jFormatter)
        jFormatter.close()
        Assertions.assertEquals(expectedResult, stringWriter.toString())
    }

    companion object {
        @JvmStatic
        fun data(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    """@rpost.MyAnnotation""",
                    """@rpost.MyAnnotation"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(true)""",
                    """@rpost.MyAnnotation(true)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation("str")""",
                    """@rpost.MyAnnotation("str")"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation('c')""",
                    """@rpost.MyAnnotation('c')"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(123)""",
                    """@rpost.MyAnnotation(123)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation((short)123)""",
                    """@rpost.MyAnnotation(((short) 123))"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(byteValue=(byte)0xaf)""",
                    """@rpost.MyAnnotation(byteValue = ((byte) 175))"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(0b1010)""",
                    """@rpost.MyAnnotation(10)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(0111)""",
                    """@rpost.MyAnnotation(73)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(123L)""",
                    """@rpost.MyAnnotation(123L)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(123.0)""",
                    """@rpost.MyAnnotation(123.0D)""" // https://github.com/javaparser/javaparser/issues/664
                ),
                Arguments.of(
                    """@rpost.MyAnnotation((float)123.0)""",
                    """@rpost.MyAnnotation(((float) 123.0D))"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(my.Enum.VALUE)""",
                    """@rpost.MyAnnotation(my.Enum.VALUE)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(null)""",
                    """@rpost.MyAnnotation(null)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation({1, 2, 3})""",
                    """@rpost.MyAnnotation({
                   |    1,
                   |    2,
                   |    3
                   |})""".trimMargin()
                ),
                Arguments.of(
                    """@rpost.MyAnnotation(value=@rpost.Nested())""",
                    """@rpost.MyAnnotation(@rpost.Nested)"""
                ),
                Arguments.of(
                    """@rpost.MyAnnotation({@rpost.Nested({'a', 'b', 'c'}), @rpost.Nested({'d'}), @rpost.Nested})""",
                    """@rpost.MyAnnotation({
                   |    @rpost.Nested({
                   |        'a',
                   |        'b',
                   |        'c'
                   |    }),
                   |    @rpost.Nested({
                   |        'd'
                   |    }),
                   |    @rpost.Nested
                   |})""".trimMargin()
                )
            )
        }
    }

    class DummyAnnotatable : Annotatable {
        private val model = JCodeModel()

        var annotation: JAnnotationUse? = null

        override fun addAnnotation(fqn: String?): JAnnotationUse? {
            annotation = Reflections.construct(
                JAnnotationUse::class.java,
                arrayOf(JClass::class.java),
                arrayOf(model.ref(fqn))
            )
            return annotation
        }

    }

}