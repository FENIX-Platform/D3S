package test.serialization2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

public class A implements Serializable {

    public B b = new B();
    public C c = new C();

    public Collection<D> d = Arrays.asList(new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D(),new D());

}
