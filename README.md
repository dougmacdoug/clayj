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
    });
});
```

The ALL_CAPS aliases exists for those who want to reuse Clay examples more easily, but the more idiomatic Java
calls exist for those who prefer a softer style.

```java
// Parent element with 8px of padding
clay(id("parent").layout(l->l .padding(p->p.all(8))), ()-> {
    // Child element 1
    clayText("Hello World", f->f.fontSize(16));
    // Child element 2 with red background
    clay(id("child").backgroundColor(COLOR_RED), ()-> {
        // etc
    });
});
```

In Clay, most of the configuration uses stack based structs. To mimic the feel of this Clayj uses 
the builder pattern in 2 forms: a functional builder for named properties,
and a simpler method call for short values/series.

A Clay struct like `{ .fontSize = 16, .color =  { 255, 0, 0, 255 } }` would be written as
 `f->f.fontSize(16).color(255, 0, 0, 255)` in Clayj.

## State of the Library

There are several Todos throughout the code and they will be added to the readme soon. (First todo)

Currently there is a demo that runs in Java25 with clay.dll(v0.14) and raylib.dll(v5.5).
It is a direct port of https://github.com/nicbarker/clay/blob/main/examples/raylib-multi-context/main.c

Testing will soon be done on linux, but it is expected to run first try as usual.

The memory management has been tested and does not allocate new memory over time but
the scoped arenas and helper classes need work both to be more easily understandable
and to be more idiomatic to Java.

The next major step will be to create a Java demo that isn't just a straight port of the
 C demo. The FFM port is rather difficult to read and is completely unnecessary in many 
 places but it was done to prove that the library can mirror the C code very closely.

## String literals

Clay uses the C preprocessor to ensure string literals are used for CLAY_STRING, but this
is not specifically supported in Java. It is up to the Clayj user to select the appropriate 
function depending on whether the input string is a literal or not.

Clayj stores string literals in the global Arena to achieve the intended effect 
internal to Clay. Incorrectly using methods designed for string literals on dynamically created strings
could have negative consequences especially if it occurs during the render loop.

```java
ClayString.literal("CLAY_STRING struct {.isStaticallyAllocated=true}");
```

## Clayj Scoped Arena

The Clayj Scoped Arena is designed to efficiently allocate storage for Clay stack based structs
in Java FFM without growing the arena every frame. Any java reference to a scoped
arena object used outside its scope may result in segfault or bad render bugs.

```java
Clay.initialize();
// scope 1
while(!WindowShouldClose()) {
  Clay.beginLayout();
  // scope 2
  clay(id("outer"), ()->{
    // scope 3
    clay(id("inner"), ()->{
        // scope 4
       });
    // scope 3
   });
   // scope 2
   var renderArray = Clay.endLayout();
// scope 1
   render(renderArray);
}
```
Any struct that you create using Clayj convenience methods will only
live for its scope. 

## Text Buffers

Text buffers need to survive beyond the layout scope because they are included
in the RenderCommands and when the actual render happens, calls to Raylib
DrawText require the text buffer.

ClayString created with String literals are only allocated once and are 
stored in the global Arena. They are usable the entire lifetime of
the application.

Clayj provides a helper allocator that efficiently reuses buffers
for dynamic strings. It supports strings up to 1024 bytes. Larger
data is the responsibility of the user. To use Clayj dynamic strings
call Clay.initializeStrings() inside the render loop before
begin layout.

Literal
```
var txt = ClayString.literal("Compile Time Literal");
```

Dyamic
```
Clay.initializeStrings();

// ...

var now = ClayString.dynamic(LocalDateTime.now().toString());

```


Shortcuts for CLAY_TEXT exist for dynamic and literal strings

```java
// explicit
clayText(ClayString.literal("Show FPS"), l->l.fontSize(10).fontId(0));
// ..
clayText(ClayString.dynamic("FPS : " + fps), l->l.fontSize(12).fontId(0));

// alternative todo: these are going to be renamed or removed
ltext("Show FPS", l->l.fontSize(10).fontId(0));
// ..
dtext("FPS : " + fps, l->l.fontSize(12).fontId(0));

```

If you handle the memory yourself or get your strings from some other library
you can create use fromCStr()

```java
// null terminated or length variants available
MemorySegement ms = someCString(myAllocator);
text(ClayString.fromCstr(ms), l->l.fontSize(12).fontId(0));
```

## Memory Management

There are several methods to handle memory without growing every frame
 of the render loop, but they are trickier if the MemorySegments need
 to live outside a certain scope. You can use a simple confined arena,
 but you need to close the arena if the memory grows or gets allocated
 frequently otherwise it will continue to grow over the course of the
 runtime. Even if you stop using a MemorySegment it does not get reclaimed
 while the Arena is open. Opening and closing an arena and allocating memory
 every frame in the render loop will negatively impact performance.
 
A SlicingArena may be the best alternative for most uses especially 
 if you know how much memory you will need per frame. Also, you can create
 and destroy arenas during certain events. Although examples using
 Arena.ofConfined typically show it being used in a try-with-resources block
 you can just create the arena during one event, and then later manually 
 close() it to return memory back to the system.

