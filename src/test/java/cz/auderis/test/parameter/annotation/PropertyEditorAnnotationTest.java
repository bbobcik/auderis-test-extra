package cz.auderis.test.parameter.annotation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.test.temp.propedit.ValueClass1;
import org.test.temp.propedit.ValueClass2;
import org.test.temp.propedit.ValueClass2EditorThatIsNotAutodiscovered;
import org.test.temp.propedit.ValueClass3;
import org.test.temp.propedit.ValueClass3RegisteredEditor;

import java.beans.PropertyEditorManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;

@RunWith(JUnitParamsRunner.class)
public class PropertyEditorAnnotationTest {

    @BeforeClass
    public static void registerPropertyEditors() {
        PropertyEditorManager.registerEditor(ValueClass3.class, ValueClass3RegisteredEditor.class);
    }

    @Test
    @Parameters({
            "1-x | 1 | x"
    })
    public void shouldUseAutodiscoveredPropertyEditor(
            @UsingPropertyEditor(of = ValueClass1.class) ValueClass1 val,
            int expectedNumber,
            String expectedText
    ) throws Exception {
        assertThat(val.getClass(), is(equalTo(ValueClass1.class)));
        assertThat(val, allOf(hasProperty("numValue", is(expectedNumber)), hasProperty("textValue", is(expectedText))));
    }

    @Test
    @Parameters({
            "3-pkeh | 3 | pkeh"
    })
    public void shouldUseRegisteredPropertyEditor(
            @UsingPropertyEditor(of = ValueClass3.class) ValueClass3 val,
            int expectedNumber,
            String expectedText
    ) throws Exception {
        assertThat(val.getClass(), is(equalTo(ValueClass3.class)));
        assertThat(val, allOf(hasProperty("numValue", is(expectedNumber)), hasProperty("textValue", is(expectedText))));
    }

    @Test
    @Parameters({
            "129-text with space | 129 | text with space"
    })
    public void shouldUseExplicitPropertyEditor(
            @UsingPropertyEditor(editor = ValueClass2EditorThatIsNotAutodiscovered.class) ValueClass2 val,
            int expectedNumber,
            String expectedText
    ) throws Exception {
        assertThat(val.getClass(), is(equalTo(ValueClass2.class)));
        assertThat(val, allOf(hasProperty("numValue", is(expectedNumber)), hasProperty("textValue", is(expectedText))));
    }

}
