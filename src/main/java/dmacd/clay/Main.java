package dmacd.clay;


import dmacd.clay.renderer.RaylibRenderer;
import dmacd.ffm.clay.*;
import dmacd.ffm.raylib.Color;
import dmacd.ffm.raylib.RayFFM;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static dmacd.clay.Clay.*;
import static dmacd.clay.renderer.RaylibRenderer.Raylib_MeasureText;
import static dmacd.ffm.raylib.RayFFM.DrawText;
import static dmacd.ffm.raylib.RayFFM.LoadFont;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    static MemorySegment cStr = Arena.global().allocateFrom("Test 123");

    static MemorySegment globalColor(int rgb) {
        var c = Arena.global().allocate(4);
        c.set(ValueLayout.JAVA_INT, 0, rgb);
        return c;
    }

    static MemorySegment RED =  globalColor(0xff0000ff);
    static MemorySegment GREEN =  globalColor(0xff00ff00);
    static MemorySegment BLUE =  globalColor(0xffff0000);
    static MemorySegment WHITE =  globalColor(0xffFFFFff);
    static MemorySegment BLACK = globalColor(0);


    static void main() {
        System.loadLibrary("raylib");

        var size = Clay.minMemorySize();
        try(var arena = TrackedArena.ofConfined()) {
            Clay.initialize(arena);
            var userTest = arena.allocateFrom("user test data");
            var mem = arena.allocate(size);
            var clay_arena = ClayFFM.Clay_CreateArenaWithCapacityAndMemory(arena, size, mem);
            var dims = Clay_Dimensions.allocate(arena);
            Clay_Dimensions.width(dims, 800);
            Clay_Dimensions.height(dims, 600);
            var errFunc = Clay_ErrorHandler.errorHandlerFunction.allocate(Clay::errorHandler, arena);
            var errorHandler = Clay_ErrorHandler.allocate(arena);
            Clay_ErrorHandler.errorHandlerFunction(errorHandler, errFunc);
            Clay_ErrorHandler.userData(errorHandler, userTest);

//            var measure = Clay_SetMeasureTextFunction$measureTextFunction.allocate(
//                    (text, config, userData)->{
//                        return Raylib_MeasureText(arena, text, config, userData);
//                    }, arena);
//            var msFont = RayFFM.LoadFont(arena, arena.allocateFrom("resources/robotto.ttf"));
//
//            ClayFFM.Clay_SetMeasureTextFunction(measure, fonts);
            ClayFFM.Clay_Initialize(clay_arena, dims, errorHandler);
            var testStr = arena.allocateFrom("bobs");
            MemorySegment clayStr = arena.allocate(Clay_String.layout());
            Clay_String.chars(clayStr, testStr);
            Clay_String.length(clayStr, 3);
            Clay_String.isStaticallyAllocated(clayStr, false);
            var ms = ClayFFM.Clay__HashString(arena, clayStr, 0, 0);

//            RayFF

            RayFFM.SetTargetFPS(60);               // Set our game to run at 60 frames-per-second
            //--------------------------------------------------------------------------------------
            RayFFM.InitWindow(800, 600, arena.allocateFrom("hello there"));
            var bgColor = Color.allocate(arena);

            var fgColor = Color.allocate(arena);

            Color.r(bgColor, (byte)0x18);
            Color.g(bgColor, (byte)0x18);
            Color.b(bgColor, (byte)0x18);

            Color.r(fgColor, (byte)0xFF);
            Color.g(fgColor, (byte)0xEE);
            Color.b(fgColor, (byte)0xEE);
            Color.a(fgColor, (byte)0xFF);

            var txtmem = arena.allocate(256);
            var renderCommandArray = Clay_RenderCommandArray.allocate(arena);
            while (!RayFFM.WindowShouldClose())    // Detect window close button or ESC key
            {
                ClayFFM.Clay_BeginLayout();
                clay(id("test"));
                ClayFFM.Clay_EndLayout((b,a)->renderCommandArray);

                var bytes = String.format("Arena: %d", arena.getTotalAllocatedBytes()).getBytes(StandardCharsets.UTF_8);
                for(int i = 0; i < bytes.length; i++) {
                    txtmem.setAtIndex(ValueLayout.JAVA_BYTE, i,  bytes[i]);
                }
                txtmem.setAtIndex(ValueLayout.JAVA_BYTE, bytes.length, (byte)0);

                // Update
                //----------------------------------------------------------------------------------
                // TODO: Update your variables here
                //----------------------------------------------------------------------------------

                // Draw
                //----------------------------------------------------------------------------------
                RayFFM.BeginDrawing();


                RayFFM.ClearBackground(bgColor);
                Color.a(fgColor, (byte)100);
                RayFFM.DrawText(txtmem, 10, 20, 20, fgColor);
                Color.a(fgColor, (byte)255);
                RayFFM.DrawText(txtmem, 10, 20, 20, fgColor);

                RayFFM.EndDrawing();
                //----------------------------------------------------------------------------------
            }

            // De-Initialization
            //--------------------------------------------------------------------------------------
            RayFFM.CloseWindow();
        }
    }
}
