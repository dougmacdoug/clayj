package dmacd.clay;

import dmacd.clay.demo.*;
import dmacd.ffm.clay.*;
import dmacd.ffm.raylib.Rayliib;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.Function;

import static dmacd.clay.Clay.*;
import static dmacd.clay.renderer.RaylibRenderer.*;
import static dmacd.ffm.clay.ClayFFM.*;
import static dmacd.ffm.raylib.RayFFM.*;

public class Demo {
    // todo: add to clayffm
    public final static  int CLAY_LEFT_TO_RIGHT = 0;
    public final static  int CLAY_TOP_TO_BOTTOM = 1;

    public static final MemorySegment STR_ROBOTO_FONT_PATH = Arena.global().allocateFrom("resources/Roboto-Regular.ttf");
    static final int FONT_ID_BODY_16 = 0;
    static final MemorySegment CLAY_COLOR_WHITE = GLOBAL_CLAY_COLOR(255, 255, 255, 255);
    static final MemorySegment BLACK = GLOBAL_RAYLIB_COLOR(0,0,0,255); // raylib color

    static final Clay.Color COLOR_WHITE = new Clay.Color(255, 255, 255,255);
//    void RenderHeaderButton(Clay_String text) {
//        CLAY({
//                .layout = { .padding = { 16, 16, 8, 8 }},
// .backgroundColor = { 140, 140, 140, 255 },
// .cornerRadius = CLAY_CORNER_RADIUS(5)
// }) {
//            CLAY_TEXT(text, CLAY_TEXT_CONFIG({
//                    .fontId = FONT_ID_BODY_16,
//                    .fontSize = 16,
//                    .textColor = { 255, 255, 255, 255 }
// }));
//        }
//    }


    static {
        System.loadLibrary("raylib");
    }

    static MemorySegment documentsRaw = Document.allocateArray(5, Arena.global());
    static MemorySegment documents = DocumentArray.allocate(Arena.global());

    static {
        DocumentArray.documents(documents, documentsRaw);
        DocumentArray.length(documents, 5);
    }

    static void intoClayString(MemorySegment clayStr, String str) {
        var cstr = Arena.global().allocateFrom(str);
        Clay_String.chars(clayStr, cstr);
        Clay_String.length(clayStr, (int) cstr.byteSize() - 1);
        Clay_String.isStaticallyAllocated(clayStr, false);
    }

    static MemorySegment ClayVideoDemo_Initialize() {
        var docArray = DocumentArray.documents(documents);
        var doc = Document.asSlice(docArray, 0);
        intoClayString(Document.title(doc), "Squirrels");
        intoClayString(Document.contents(doc), "The Secret Life of Squirrels: Nature's Clever Acrobats\n" +
                "Squirrels are often overlooked creatures, dismissed as mere park inhabitants or backyard nuisances. Yet, beneath their fluffy tails and twitching noses lies an intricate world of cunning, agility, and survival tactics that are nothing short of fascinating. As one of the most common mammals in North America, squirrels have adapted to a wide range of environments from bustling urban centers to tranquil forests and have developed a variety of unique behaviors that continue to intrigue scientists and nature enthusiasts alike.\n" +
                "\n" +
                "Master Tree Climbers\n" +
                "At the heart of a squirrel's skill set is its impressive ability to navigate trees with ease. Whether they're darting from branch to branch or leaping across wide gaps, squirrels possess an innate talent for acrobatics. Their powerful hind legs, which are longer than their front legs, give them remarkable jumping power. With a tail that acts as a counterbalance, squirrels can leap distances of up to ten times the length of their body, making them some of the best aerial acrobats in the animal kingdom.\n" +
                "But it's not just their agility that makes them exceptional climbers. Squirrels' sharp, curved claws allow them to grip tree bark with precision, while the soft pads on their feet provide traction on slippery surfaces. Their ability to run at high speeds and scale vertical trunks with ease is a testament to the evolutionary adaptations that have made them so successful in their arboreal habitats.\n" +
                "\n" +
                "Food Hoarders Extraordinaire\n" +
                "Squirrels are often seen frantically gathering nuts, seeds, and even fungi in preparation for winter. While this behavior may seem like instinctual hoarding, it is actually a survival strategy that has been honed over millions of years. Known as \"scatter hoarding,\" squirrels store their food in a variety of hidden locations, often burying it deep in the soil or stashing it in hollowed-out tree trunks.\n" +
                "Interestingly, squirrels have an incredible memory for the locations of their caches. Research has shown that they can remember thousands of hiding spots, often returning to them months later when food is scarce. However, they don't always recover every stash some forgotten caches eventually sprout into new trees, contributing to forest regeneration. This unintentional role as forest gardeners highlights the ecological importance of squirrels in their ecosystems.\n" +
                "\n" +
                "The Great Squirrel Debate: Urban vs. Wild\n" +
                "While squirrels are most commonly associated with rural or wooded areas, their adaptability has allowed them to thrive in urban environments as well. In cities, squirrels have become adept at finding food sources in places like parks, streets, and even garbage cans. However, their urban counterparts face unique challenges, including traffic, predators, and the lack of natural shelters. Despite these obstacles, squirrels in urban areas are often observed using human infrastructure such as buildings, bridges, and power lines as highways for their acrobatic escapades.\n"+
                "There is, however, a growing concern regarding the impact of urban life on squirrel populations. Pollution, deforestation, and the loss of natural habitats are making it more difficult for squirrels to find adequate food and shelter. As a result, conservationists are focusing on creating squirrel-friendly spaces within cities, with the goal of ensuring these resourceful creatures continue to thrive in both rural and urban landscapes.\n"+
                "\n"+
                "A Symbol of Resilience\n"+
                "In many cultures, squirrels are symbols of resourcefulness, adaptability, and preparation. Their ability to thrive in a variety of environments while navigating challenges with agility and grace serves as a reminder of the resilience inherent in nature. Whether you encounter them in a quiet forest, a city park, or your own backyard, squirrels are creatures that never fail to amaze with their endless energy and ingenuity.\n"+
                "In the end, squirrels may be small, but they are mighty in their ability to survive and thrive in a world that is constantly changing. So next time you spot one hopping across a branch or darting across your lawn, take a moment to appreciate the remarkable acrobat at work a true marvel of the natural world.\n");

        doc = Document.asSlice(docArray, 1);
        intoClayString(Document.title(doc), "Lorem Ipsum");
        intoClayString(Document.contents(doc), "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        doc = Document.asSlice(docArray, 2);
        intoClayString(Document.title(doc), "Vacuum Instructions");
        intoClayString(Document.contents(doc), "Chapter 3: Getting Started - Unpacking and Setup\n"+
                        "\n"+
                        "Congratulations on your new SuperClean Pro 5000 vacuum cleaner! In this section, we will guide you through the simple steps to get your vacuum up and running. Before you begin, please ensure that you have all the components listed in the \"Package Contents\" section on page 2.\n"+
                        "\n"+
                        "1. Unboxing Your Vacuum\n"+
                        "Carefully remove the vacuum cleaner from the box. Avoid using sharp objects that could damage the product. Once removed, place the unit on a flat, stable surface to proceed with the setup. Inside the box, you should find:\n"+
                        "\n"+
                        "    The main vacuum unit\n"+
                        "    A telescoping extension wand\n"+
                        "    A set of specialized cleaning tools (crevice tool, upholstery brush, etc.)\n"+
                        "    A reusable dust bag (if applicable)\n"+
                        "    A power cord with a 3-prong plug\n"+
                        "    A set of quick-start instructions\n"+
                        "\n"+
                        "2. Assembling Your Vacuum\n"+
                        "Begin by attaching the extension wand to the main body of the vacuum cleaner. Line up the connectors and twist the wand into place until you hear a click. Next, select the desired cleaning tool and firmly attach it to the wand's end, ensuring it is securely locked in.\n"+
                        "\n"+
                        "For models that require a dust bag, slide the bag into the compartment at the back of the vacuum, making sure it is properly aligned with the internal mechanism. If your vacuum uses a bagless system, ensure the dust container is correctly seated and locked in place before use.\n"+
                        "\n"+
                        "3. Powering On\n"+
                        "To start the vacuum, plug the power cord into a grounded electrical outlet. Once plugged in, locate the power switch, usually positioned on the side of the handle or body of the unit, depending on your model. Press the switch to the \"On\" position, and you should hear the motor begin to hum. If the vacuum does not power on, check that the power cord is securely plugged in, and ensure there are no blockages in the power switch.\n"+
                        "\n"+
                        "Note: Before first use, ensure that the vacuum filter (if your model has one) is properly installed. If unsure, refer to \"Section 5: Maintenance\" for filter installation instructions.");

        doc = Document.asSlice(docArray, 3);
        intoClayString(Document.title(doc), "Article 4");
        intoClayString(Document.contents(doc), "Article 4");

        doc = Document.asSlice(docArray, 4);
        intoClayString(Document.title(doc), "Article 5");
        intoClayString(Document.contents(doc), "Article 5");

        var data = ClayVideoDemo_Data.allocate(Arena.global());
        var frameArena = ClayVideoDemo_Data.frameArena(data);
        MEMORY_ADDRESS = Arena.global().allocate(1024).address();
        ClayVideoDemo_Arena.memory(frameArena, MEMORY_ADDRESS);
        return data;
    }
static long MEMORY_ADDRESS;

    public static void main(String argv[]) {

        Clay_Raylib_Initialize(1024, 768, Arena.global().allocateFrom("Introducing Clay Demo"),
                FLAG_WINDOW_RESIZABLE() | FLAG_WINDOW_HIGHDPI() | FLAG_MSAA_4X_HINT() | FLAG_VSYNC_HINT()); // Extra parameters to this function are new since the video was published

        try (Arena arena = Arena.ofConfined()) {
            Clay.initialize(arena);
            var fonts = Rayliib.Font.allocateArray(1, arena);
            // technically same address
            var font = LoadFontEx(arena, STR_ROBOTO_FONT_PATH, 48,
                    MemorySegment.NULL, 400);
            MemorySegment.copy(font, 0, fonts, 0, Rayliib.Font.layout().byteSize());

            font = Rayliib.Font.asSlice(fonts, FONT_ID_BODY_16);
//         *     printf("font %p", &fonts[FONT_ID_BODY_16]);
//         *     printf("font %p", &fonts[FONT_ID_BODY_16].texture);
            SetTextureFilter(Rayliib.Font.texture(font), TEXTURE_FILTER_BILINEAR());

            long clayRequiredMemory = Clay_MinMemorySize();
            var clayMemoryTop = Clay_CreateArenaWithCapacityAndMemory(arena, clayRequiredMemory,
                    arena.allocate(clayRequiredMemory));
            var dims = Clay_Dimensions.allocate(arena);
            Clay_Dimensions.width(dims, GetScreenWidth());
            Clay_Dimensions.height(dims, GetScreenHeight() / 2);
            var errHandler = Clay_ErrorHandler.allocate(arena);
            var errFunc = Clay_ErrorHandler.errorHandlerFunction.allocate(Clay::errorHandler, arena);
            Clay_ErrorHandler.errorHandlerFunction(errHandler, errFunc);
            var clayContextTop = Clay_Initialize(clayMemoryTop, dims, errHandler); // This final argument is new since the video was published

            var dataTop = ClayVideoDemo_Initialize();

            var measureTextFunc = Clay_SetMeasureTextFunction$measureTextFunction.allocate((a, b, c) -> {
                return Raylib_MeasureText(arena, a, b, c);
            }, arena);
            Clay_SetMeasureTextFunction(measureTextFunc, font);

            var clayMemoryBottom = Clay_CreateArenaWithCapacityAndMemory(arena, clayRequiredMemory, arena.allocate(clayRequiredMemory));
            // todo: in practice, possibly better to clone dims
            var clayContextBottom = Clay_Initialize(clayMemoryBottom, dims, errHandler); // This final argument is new since the video was published
            var dataBottom = ClayVideoDemo_Initialize();
            Clay_SetMeasureTextFunction(measureTextFunc, fonts);

            while (!WindowShouldClose()) {
                ClayVideoDemo_Data.yOffset(dataBottom, (float) (GetScreenHeight() / 2));
                var renderCommandsTop = CreateLayout(clayContextTop, dataTop); //Clay_RenderCommandArray
                var renderCommandsBottom = CreateLayout(clayContextBottom, dataBottom);
                BeginDrawing();
                ClearBackground(BLACK);
                Clay_Raylib_Render(renderCommandsTop, fonts);
                Clay_Raylib_Render(renderCommandsBottom, fonts);
                EndDrawing();
            }

            Clay_Raylib_Close();


            return;
        }
   }
//    static MemorySegment layoutDimensions = Clay_Dimensions.allocate(SHARED_ARENA);
    static final MemorySegment mousePosition = Rayliib.Vector2.allocate(Arena.global());
    static final MemorySegment mouseScroll = Rayliib.Vector2.allocate(Arena.global());
//    static final MemorySegment pointerState = Clay_Vector2.allocate(SHARED_ARENA);
//    static final MemorySegment scrollState = Clay_Vector2.allocate(SHARED_ARENA);

   static
/*    Clay_RenderCommandArray */ MemorySegment
    CreateLayout(MemorySegment context, /*(ClayVideoDemo_Data *)*/ MemorySegment data) {
        Clay_SetCurrentContext(context);
        Clay_SetDebugModeEnabled(true);

       Clay_SetLayoutDimensions(Dimensions.of(GetScreenWidth(), GetScreenHeight() / 2));
       MemorySegment mp = GetMousePosition((a,b) ->mousePosition);

        Rayliib.Vector2.y(mp, Rayliib.Vector2.y(mp) - ClayVideoDemo_Data.yOffset(data));
        var scrollDelta = GetMouseWheelMoveV((a,b)->mouseScroll);

       Clay_SetPointerState(Vector2.fromRaylib(mp), IsMouseButtonDown(0));
       var scrollState = Vector2.fromRaylib(scrollDelta);
       Clay_UpdateScrollContainers(true, scrollState, GetFrameTime());
       return ClayVideoDemo_CreateLayout(data);
    }

    static void RenderDropdownMenuItem(String str) {
        CLAY(id("").layout(l -> l
                .padding(CLAY_PADDING_ALL(16))), () -> {
            CLAY_TEXT(str, cfg -> cfg
                    .fontId(FONT_ID_BODY_16)
                    .fontSize(16)
                    .textColor(255, 255, 255, 255)
            );
        });
    }

    static void RenderHeaderButton(String text) {
        CLAY(id("") // todo: anon()
                .layout(l -> l
                        .padding(16, 16, 8, 8))
                .backgroundColor(140, 140, 140, 255)
                .cornerRadius(CLAY_CORNER_RADIUS(5)), () -> {
            CLAY_TEXT(text, cfg -> cfg
                    .fontId(FONT_ID_BODY_16)
                    .fontSize(16)
                    .textColor(255, 255, 255, 255));
        });
    }
    static void HandleSidebarInteraction(
            MemorySegment elementId,
            MemorySegment pointerData,
            long userData
    ) {
        var msUserData = MemorySegment.ofAddress(userData);
        var clickData = SidebarClickData.reinterpret(msUserData, arena(), p->{});

        // If this button was clicked
        if (Clay_PointerData.state(pointerData) == CLAY_POINTER_DATA_PRESSED_THIS_FRAME()) {
            var requestDocumentIndex = SidebarClickData.requestedDocumentIndex(clickData);
            if (requestDocumentIndex >= 0 && requestDocumentIndex < DocumentArray.length(documents)) {
                // Select the corresponding document
                var ptr = SidebarClickData.selectedDocumentIndex(clickData);
                ptr.setAtIndex(ValueLayout.JAVA_INT, 0, requestDocumentIndex); // todo: test
            }
        }
    }


    static
    /*Clay_RenderCommandArray*/MemorySegment ClayVideoDemo_CreateLayout(/*(ClayVideoDemo_Data *)*/ MemorySegment data) {
        var frameArena = ClayVideoDemo_Data.frameArena(data);
//        var selectedDocumentIndex = ClayVideoDemo_Data.selectedDocumentIndex(data);
        ClayVideoDemo_Arena.offset(frameArena, 0);

        Clay_BeginLayout();

// applies the setting directly to the memory segment
        Function<LayoutConfig.Sizing, LayoutConfig.Sizing> layoutExpand = (s) -> s
                .width(LayoutConfig.Sizing.grow(0))
                .height(LayoutConfig.Sizing.grow(0));

        final Clay.Color contentBackgroundColor = new Clay.Color(90, 90, 90, 255);

        // Build UI here
        CLAY(id("OuterContainer")
                .backgroundColor(43, 41, 51, 255)
                .layout(l -> l
                        .layoutDirection(CLAY_TOP_TO_BOTTOM)
                        .sizing(layoutExpand)
                        .padding(CLAY_PADDING_ALL(16))
                        .childGap(16)
                ), () -> {
            // Child elements go inside braces
            CLAY(id("HeaderBar")
                    .layout(l -> l
                                    .sizing(s -> s
                                            .height(CLAY_SIZING_FIXED(60))
                                            .width(CLAY_SIZING_GROW(0))
                                    )
                                    .padding(16, 16, 8, 8)
                                    .childGap(16)
                            // todo: childAlignment
//                    .childAlignment(a->a) = {
//                        .y = CLAY_ALIGN_Y_CENTER
//                    }
                    )
                    .backgroundColor(contentBackgroundColor)
                    .cornerRadius(CLAY_CORNER_RADIUS(8)), () -> {
                // Header buttons go here
                CLAY(id("FileButton")
                        .layout(l -> l.padding(16, 16, 8, 8))
                        .backgroundColor(140, 140, 140, 255)
                        .cornerRadius(CLAY_CORNER_RADIUS(5)), () -> {
                    CLAY_TEXT("File", cfg -> cfg
                            .fontId(FONT_ID_BODY_16)
                            .fontSize(16)
                            .textColor(255, 255, 255, 255)
                    );
                    int CLAY_ATTACH_TO_PARENT = 1;
                    boolean fileMenuVisible =
                            Clay_PointerOver(getElementId("FileButton"))
                                    ||
                                    Clay_PointerOver(getElementId("FileMenu"));

                    if (fileMenuVisible) { // Below has been changed slightly to fix the small bug where the menu would dismiss when mousing over the top gap
                        CLAY(id("FileMenu")
                                .floating(f -> f
                                        .attachTo(CLAY_ATTACH_TO_PARENT)
                                        .attachPoints(a -> a
                                                .parent(CLAY_ATTACH_POINT_LEFT_BOTTOM())
                                        )
                                )
                                .layout(l -> l
                                        .padding(0, 0, 8, 8)
                                ), () -> {
                            CLAY(id("")
                                    .layout(l -> l
                                            .layoutDirection(CLAY_TOP_TO_BOTTOM)
                                            .sizing(s -> s
                                                    .width(CLAY_SIZING_FIXED(200))
                                            )
                                    )
                                    .backgroundColor(40, 40, 40, 255)
                                    .cornerRadius(CLAY_CORNER_RADIUS(8)), () -> {
                                // Render dropdown items here
                                RenderDropdownMenuItem("New");
                                RenderDropdownMenuItem("Open");
                                RenderDropdownMenuItem("Close");
                            });
                        });// filemenu
                    }

                    RenderHeaderButton("Edit");
                    CLAY(id("").layout(l -> l.sizing(CLAY_SIZING_GROW(0))), () -> {
                    });
                    RenderHeaderButton("Upload");
                    RenderHeaderButton("Media");
                    RenderHeaderButton("Support");
                }); // end header bar

                CLAY(id("LowerContent")
                        .layout(l -> l.sizing(layoutExpand).childGap(16)), () -> {
                    CLAY(id("Sidebar")
                            .backgroundColor(contentBackgroundColor)
                            .layout(l -> l
                                    .layoutDirection(CLAY_TOP_TO_BOTTOM)
                                    .padding(CLAY_PADDING_ALL(16))
                                    .childGap(8)
                                    .sizing(s -> s
                                            .width(CLAY_SIZING_FIXED(250))
                                            .height(CLAY_SIZING_GROW(0))
                                    )), () -> {
                        Function<LayoutConfig, LayoutConfig> sidebarButtonLayout = l -> l
                                .sizing(s -> s.width(CLAY_SIZING_GROW(0)))
                                .padding(CLAY_PADDING_ALL(16));
                        for (int i = 0; i < 5; i++) {
                            var docs = DocumentArray.documents(documents);
                            var document = Document.asSlice(docs, i);

                            if (i == ClayVideoDemo_Data.selectedDocumentIndex(data)) {
                                CLAY(id("").layout(sidebarButtonLayout)
                                        .backgroundColor(120, 120, 120, 255)
                                        .cornerRadius(CLAY_CORNER_RADIUS(8)), () -> {
                                    CLAY_TEXT(Document.title(document), cfg -> cfg
                                            .fontId(FONT_ID_BODY_16)
                                            .fontSize(20)
                                            .textColor(255, 255, 255, 255));
                                });
                            } else {
                                var offset = ClayVideoDemo_Arena.offset(frameArena);
                                var memAddr = ClayVideoDemo_Arena.memory(frameArena);
                                var memory = MemorySegment.ofAddress(memAddr);
                                memory = memory.reinterpret(1024, Arena.global(), p->{});
                                var chunkSize = SidebarClickData.layout().byteSize();
                                var clickData = memory.asSlice(offset, chunkSize);

                                var selectedDocIndexPtr = data.asSlice(ClayVideoDemo_Data.selectedDocumentIndex$offset(), ValueLayout.JAVA_INT);
//        *clickData = (SidebarClickData) { .requestedDocumentIndex = i, .selectedDocumentIndex = &data->selectedDocumentIndex };
//        data->frameArena.offset += sizeof(SidebarClickData);
                                SidebarClickData.requestedDocumentIndex(clickData, i);
                                SidebarClickData.selectedDocumentIndex(clickData, selectedDocIndexPtr);
                                ClayVideoDemo_Arena.offset(frameArena, offset + SidebarClickData.layout().byteSize());

                                CLAY(id("").layout(sidebarButtonLayout)
                                        .backgroundColor(120, 120, 120, Clay_Hovered() ? 120 : 0)
                                        .cornerRadius(CLAY_CORNER_RADIUS(8)), () -> {
                                    var hoverFunc = Clay_OnHover$onHoverFunction.allocate(Demo::HandleSidebarInteraction, arena());
                                    Clay_OnHover(hoverFunc, clickData.address());
                                    CLAY_TEXT(Document.title(document), c -> c
                                            .fontId(FONT_ID_BODY_16)
                                            .fontSize(20)
                                            .textColor(255, 255, 255, 255));

                                });
                            }
                        }
                    });
                });
                CLAY(id("MainContent").backgroundColor(contentBackgroundColor)
                        .clip(c -> c
                                .vertical(true)
                                .childOffset(Clay_GetScrollOffset(arena())))
                        .layout(l -> l
                                .layoutDirection(CLAY_TOP_TO_BOTTOM)
                                .childGap(16)
                                .padding(CLAY_PADDING_ALL(16))
                                .sizing(layoutExpand)), () -> {
                    var docs = DocumentArray.documents(documents);
                    var selectedDocument = Document.asSlice(docs, ClayVideoDemo_Data.selectedDocumentIndex(data));
                    CLAY_TEXT(Document.title(selectedDocument), c -> c
                            .fontId(FONT_ID_BODY_16)
                            .fontSize(24)
                            .textColor(COLOR_WHITE));
                    CLAY_TEXT(Document.contents(selectedDocument), c -> c
                            .fontId(FONT_ID_BODY_16)
                            .fontSize(24)
                            .textColor(COLOR_WHITE));

                });
            });
        });

        var renderCommands = Clay_EndLayout(arena());
        for (int i = 0; i < Clay_RenderCommandArray.length(renderCommands); i++) {
            var cmd = Clay_RenderCommandArray_Get(renderCommands, i);
            var bb = Clay_RenderCommand.boundingBox(cmd);
            Clay_BoundingBox.y(bb, Clay_BoundingBox.y(bb) + ClayVideoDemo_Data.yOffset(data));
        }
        return renderCommands;
    }
}