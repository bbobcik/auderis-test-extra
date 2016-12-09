package org.test.temp.propedit;

public class ValueClass2EditorThatIsNotAutodiscovered extends ValueClass1Editor {

    @Override
    protected ValueClass1 createResult() {
        return new ValueClass2();
    }

}
