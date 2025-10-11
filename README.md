# Welcome to Clayj 
*(improves and rhymes with age)* 
 
**Java bindings for Clay, the awesome UI Layout Library by Nic Barker.**

Clay Github Project: https://github.com/nicbarker/clay

Clay Website: https://www.nicbarker.com/clay

First, go to those websites and checkout Clay and only come back if you think it's awesome.

Ok, so you're back.

This project doesn't just make Clay available in Java,
it attempts to make it as close as reasonable to the Clay declarative syntax
while using Java FFM binding under the hood. The result splits the difference
between idiomatic Java and the Clay declarative style.

So why? Well, plenty of people have lots of experience in Java, but let's be
honest, it's usually from university or their day job.

You can go elsewhere to argue the pros and cons of a particular language,
but Java is a very comfortable language for many developers.

Clayj is for those who are comfortable with Java but would like to build more
performant C style applications, including games or other presentation software.

Here is an example in Clay
```C
// Parent element with 8px of padding
CLAY(CLAY_ID("parent"), { .layout = { .padding = CLAY_PADDING_ALL(8) } }) {
    // Child element 1
    CLAY_TEXT(CLAY_STRING("Hello World"), CLAY_TEXT_CONFIG({ .fontSize = 16 }));
    // Child element 2 with red background
    CLAY((CLAY_ID("child"), { .backgroundColor = COLOR_RED }) {
        // etc
    }
}
```

And this is the same code in Clayj
```java
// Parent element with 8px of padding
CLAY(CLAY_ID("parent").layout(l->l .padding(CLAY_PADDING_ALL(8)) ), ()-> {
    // Child element 1
    CLAY_TEXT(CLAY_STRING("Hello World"), CLAY_TEXT_CONFIG(f->f.fontSize(16)));
    // Child element 2 with red background
    CLAY(CLAY_ID("child").backgroundColor(COLOR_RED), ()-> {
        // etc
    }
}
```

The ALL_CAPS aliases exists for those who want to reuse Clay examples more easily, but the more idiomatic Java
calls exist for those who prefer a softer style.

```java
// Parent element with 8px of padding
clay(id("parent").layout(l->l .padding(p->p.all(8))), ()-> {
    // Child element 1
    text("Hello World", f->f.fontSize(16));
    // Child element 2 with red background
    clay(id("child").backgroundColor(COLOR_RED), ()-> {
        // etc
    }
}
```

In Clay, most of the configuration uses stack based structs. To mimic the feel of this Clayj uses 
the builder pattern in 2 forms: a functional builder for named properties,
and a simpler method call for short values/series.

A Clay struct like `{ .fontSize = 16, .color =  { 255, 0, 0, 255 } }` would be written as
 `f->f.fontSize(16).color(255, 0, 0, 255)` in Clayj.
