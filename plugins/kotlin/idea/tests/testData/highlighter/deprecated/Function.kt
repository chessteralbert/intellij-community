fun test() {
    <info descr="'fun test1()' is deprecated">test1</info>()
    MyClass().<info descr="'fun test2()' is deprecated">test2</info>()
    MyClass.<info descr="'fun test3()' is deprecated">test3</info>()

    <info descr="'fun test4(x : jet.Int, y : jet.Int)' is deprecated">test4</info>(1, 2)
}

Deprecated fun test1() { }
Deprecated fun test4(x: Int, y: Int) { }

class MyClass() {
    Deprecated fun test2() {}

    class object {
        Deprecated fun test3() {}
    }
}