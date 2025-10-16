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
live for its scope. The scoped arena can also be used for small
text objects. Anything under 256 bytes is fine, 256-1k is user discretion
and over 1k is unsupported.

*Do not use scoped arena for large data or text buffers.*

## Temp Buffer without Allocating every frame

```java
MemorySegment largeBuffer = null;
Arena largeBufferArena - null;
while(!WindowShouldClose()) {
  if(showLargeThing) {
    if(largeBufferArena == null) {
        largeBufferArena = Arena.ofConfined();
        largeBuffer = largeBufferArena.allocate(BIG_NUM);
    }      
    // use large buffer @ 60FPS
    showBigThing(largeBuffer);
    if(someEventHappened()) {
        showLargeThing = false;
        largeBuffer = null;
        largeBufferArena.close();
        largeBufferArena = null;
    }
  }    
  // render normal stuff here  
}
```