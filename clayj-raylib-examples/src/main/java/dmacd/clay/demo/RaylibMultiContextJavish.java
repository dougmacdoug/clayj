package dmacd.clay.demo;

import dmacd.clay.Clay;
import dmacd.clay.Clay.LayoutConfig.Sizing;
import dmacd.clay.demo.ffm.*;
import dmacd.clay.demo.util.DemoDocument;
import dmacd.clay.demo.util.VideoDemoData;
import dmacd.clay.renderer.RaylibRenderer;
import dmacd.ffm.clay.*;
import dmacd.ffm.raylib.RayFFM;
import dmacd.ffm.raylib.Raylib;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dmacd.clay.Clay.*;
import static dmacd.clay.Clay.id;

/**
 * This class is a Java/Java-ish copy of the Clay demo below:<br>
 * https://github.com/nicbarker/clay/blob/main/examples/raylib-multi-context/main.c
 * <p>
 * This version tries to remove most of the FFM that makes {@link RaylibMultiContextPort }
 * difficult to understand.
 *
 */
public class RaylibMultiContextJavish {
    static {
        System.loadLibrary("raylib");
    }

    public static final MemorySegment STR_ROBOTO_FONT_PATH = Arena.global().allocateFrom("resources/Roboto-Regular.ttf");
    static final int FONT_ID_BODY_16 = 0;
    static final MemorySegment BLACK = RaylibRenderer.globalRaylibColor(0, 0, 0, 255); // raylib color
    // todo: fix this nonsense.. (other scoped return memorysegment but maybe they should all just be java class
    static final Color COLOR_WHITE = Color.from(Clay.GLOBAL_CLAY_COLOR(255, 255, 255, 255));

    static VideoDemoData ClayVideoDemo_Initialize() {
        var documents = new ArrayList<DemoDocument>(5);
        documents.add(new DemoDocument("Squirrels", "The Secret Life of Squirrels: Nature's Clever Acrobats\n" +
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
                "While squirrels are most commonly associated with rural or wooded areas, their adaptability has allowed them to thrive in urban environments as well. In cities, squirrels have become adept at finding food sources in places like parks, streets, and even garbage cans. However, their urban counterparts face unique challenges, including traffic, predators, and the lack of natural shelters. Despite these obstacles, squirrels in urban areas are often observed using human infrastructure such as buildings, bridges, and power lines as highways for their acrobatic escapades.\n" +
                "There is, however, a growing concern regarding the impact of urban life on squirrel populations. Pollution, deforestation, and the loss of natural habitats are making it more difficult for squirrels to find adequate food and shelter. As a result, conservationists are focusing on creating squirrel-friendly spaces within cities, with the goal of ensuring these resourceful creatures continue to thrive in both rural and urban landscapes.\n" +
                "\n" +
                "A Symbol of Resilience\n" +
                "In many cultures, squirrels are symbols of resourcefulness, adaptability, and preparation. Their ability to thrive in a variety of environments while navigating challenges with agility and grace serves as a reminder of the resilience inherent in nature. Whether you encounter them in a quiet forest, a city park, or your own backyard, squirrels are creatures that never fail to amaze with their endless energy and ingenuity.\n" +
                "In the end, squirrels may be small, but they are mighty in their ability to survive and thrive in a world that is constantly changing. So next time you spot one hopping across a branch or darting across your lawn, take a moment to appreciate the remarkable acrobat at work a true marvel of the natural world.\n")
        );
        documents.add(new DemoDocument("Lorem Ipsum",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        );

        documents.add(new DemoDocument("Vacuum Instructions",
                "Chapter 3: Getting Started - Unpacking and Setup\n" +
                        "\n" +
                        "Congratulations on your new SuperClean Pro 5000 vacuum cleaner! In this section, we will guide you through the simple steps to get your vacuum up and running. Before you begin, please ensure that you have all the components listed in the \"Package Contents\" section on page 2.\n" +
                        "\n" +
                        "1. Unboxing Your Vacuum\n" +
                        "Carefully remove the vacuum cleaner from the box. Avoid using sharp objects that could damage the product. Once removed, place the unit on a flat, stable surface to proceed with the setup. Inside the box, you should find:\n" +
                        "\n" +
                        "    The main vacuum unit\n" +
                        "    A telescoping extension wand\n" +
                        "    A set of specialized cleaning tools (crevice tool, upholstery brush, etc.)\n" +
                        "    A reusable dust bag (if applicable)\n" +
                        "    A power cord with a 3-prong plug\n" +
                        "    A set of quick-start instructions\n" +
                        "\n" +
                        "2. Assembling Your Vacuum\n" +
                        "Begin by attaching the extension wand to the main body of the vacuum cleaner. Line up the connectors and twist the wand into place until you hear a click. Next, select the desired cleaning tool and firmly attach it to the wand's end, ensuring it is securely locked in.\n" +
                        "\n" +
                        "For models that require a dust bag, slide the bag into the compartment at the back of the vacuum, making sure it is properly aligned with the internal mechanism. If your vacuum uses a bagless system, ensure the dust container is correctly seated and locked in place before use.\n" +
                        "\n" +
                        "3. Powering On\n" +
                        "To start the vacuum, plug the power cord into a grounded electrical outlet. Once plugged in, locate the power switch, usually positioned on the side of the handle or body of the unit, depending on your model. Press the switch to the \"On\" position, and you should hear the motor begin to hum. If the vacuum does not power on, check that the power cord is securely plugged in, and ensure there are no blockages in the power switch.\n" +
                        "\n" +
                        "Note: Before first use, ensure that the vacuum filter (if your model has one) is properly installed. If unsure, refer to \"Section 5: Maintenance\" for filter installation instructions.")
        );
        documents.add(new DemoDocument("Article 4", "Article 4"));
        documents.add(new DemoDocument("Article 5", "Article 5"));
        return new VideoDemoData(documents);
    }

    static void main(String argv[]) {
        var arena = RaylibRenderer.Clay_Raylib_Initialize(1024, 768, "Introducing Clay Demo",
                RayFFM.FLAG_WINDOW_RESIZABLE() | RayFFM.FLAG_WINDOW_HIGHDPI() | RayFFM.FLAG_MSAA_4X_HINT() | RayFFM.FLAG_VSYNC_HINT()); // Extra parameters to this function are new since the video was published
        var fonts = Raylib.Font.allocateArray(1, arena);
        var font = RayFFM.LoadFontEx((b, a)->fonts, STR_ROBOTO_FONT_PATH, 48,
                MemorySegment.NULL, 400);
        RayFFM.SetTextureFilter(Raylib.Font.texture(font), RayFFM.TEXTURE_FILTER_BILINEAR());

        long clayRequiredMemory = ClayFFM.Clay_MinMemorySize();
        var clayMemoryTop = ClayFFM.Clay_CreateArenaWithCapacityAndMemory(arena, clayRequiredMemory,
                arena.allocate(clayRequiredMemory));
        var dims = Dimensions.of(RayFFM.GetScreenWidth(), (float) (RayFFM.GetScreenHeight() / 2));
        var errHandler = Clay_ErrorHandler.allocate(arena);
        var errFunc = Clay_ErrorHandler.errorHandlerFunction.allocate(Clay::errorHandler, arena);
        Clay_ErrorHandler.errorHandlerFunction(errHandler, errFunc);
        var clayContextTop = ClayFFM.Clay_Initialize(clayMemoryTop, dims, errHandler); // This final argument is new since the video was published

        var dataTop = ClayVideoDemo_Initialize();

        var measureTextFunc = Clay_SetMeasureTextFunction$measureTextFunction.allocate(RaylibRenderer::Raylib_MeasureText, arena);
        ClayFFM.Clay_SetMeasureTextFunction(measureTextFunc, fonts);

        var clayMemoryBottom = ClayFFM.Clay_CreateArenaWithCapacityAndMemory(arena, clayRequiredMemory, arena.allocate(clayRequiredMemory));
        // todo: in practice, possibly better to clone dims
        var clayContextBottom = ClayFFM.Clay_Initialize(clayMemoryBottom, dims, errHandler); // This final argument is new since the video was published
        var dataBottom = ClayVideoDemo_Initialize();
        ClayFFM.Clay_SetMeasureTextFunction(measureTextFunc, fonts);

        while (!RayFFM.WindowShouldClose()) {
            Clay.beginRenderLoop();
            var renderCommandsTop = CreateLayout(clayContextTop, dataTop, 0); //Clay_RenderCommandArray
            var renderCommandsBottom = CreateLayout(clayContextBottom, dataBottom, RayFFM.GetScreenHeight() / 2);
            RayFFM.BeginDrawing();
            RayFFM.ClearBackground(BLACK);
            RaylibRenderer.Clay_Raylib_Render(renderCommandsTop, fonts);
            RaylibRenderer.Clay_Raylib_Render(renderCommandsBottom, fonts);
            RayFFM.EndDrawing();
// todo: more clay methods (isdebugvis etc)
            if (RayFFM.IsKeyPressed(300)) { // f11 (f12 defaults to screenshot in raylib
                showDebug = !showDebug;
            }
        }

        RaylibRenderer.Clay_Raylib_Close();
    }

    static final MemorySegment mousePosition = Raylib.Vector2.allocate(Arena.global());
    static final MemorySegment mouseScroll = Raylib.Vector2.allocate(Arena.global());

    static Vector2 vector2FromRaylibV2(MemorySegment ms) {
        return Vector2.scoped(Raylib.Vector2.x(ms), Raylib.Vector2.y(ms));
    }

    static boolean showDebug = true;

    static
        /*    Clay_RenderCommandArray */ List<RenderCommand>
    CreateLayout(MemorySegment context, VideoDemoData data, int yOffset) {
        ClayFFM.Clay_SetCurrentContext(context);
        ClayFFM.Clay_SetDebugModeEnabled(showDebug);

        ClayFFM.Clay_SetLayoutDimensions(Dimensions.of(RayFFM.GetScreenWidth(), RayFFM.GetScreenHeight() / 2));
        MemorySegment mp = RayFFM.GetMousePosition((b, a)->mousePosition);

        Raylib.Vector2.y(mp, Raylib.Vector2.y(mp) - yOffset);
        var scrollDelta = RayFFM.GetMouseWheelMoveV((b, a)->mouseScroll);

        ClayFFM.Clay_SetPointerState(vector2FromRaylibV2(mp).ms(), RayFFM.IsMouseButtonDown(0));
        ClayFFM.Clay_UpdateScrollContainers(true,
                vector2FromRaylibV2(scrollDelta).ms(), RayFFM.GetFrameTime());
        return ClayVideoDemo_CreateLayout(data, yOffset);
    }

    static void RenderDropdownMenuItem(String str) {
        RenderDropdownMenuItem(ClayString.literal(str));
    }

    static void RenderDropdownMenuItem(ClayString str) {
        clay(id().layout(l->l
                .padding(Clay.CLAY_PADDING_ALL(16))), ()->{
            clayText(str, cfg->cfg
                    .fontId(FONT_ID_BODY_16)
                    .fontSize(16)
                    .textColor(255, 255, 255, 255)
            );
        });
    }

    /**
     * @param text must be string literal
     */
    static void RenderHeaderButton(String text) {
        RenderHeaderButton(ClayString.literal(text));
    }

    static void RenderHeaderButton(ClayString text) {
        clay(id()
                .layout(l->l
                        .padding(16, 16, 8, 8))
                .backgroundColor(140, 140, 140, 255)
                .cornerRadius(Clay.CLAY_CORNER_RADIUS(5)), ()->{
            clayText(text, cfg->cfg
                    .fontId(FONT_ID_BODY_16)
                    .fontSize(16)
                    .textColor(255, 255, 255, 255));
        });
    }

    static Vector2 scrollOffset = new Vector2(Clay_Vector2.allocate(Arena.global()));

    static List<RenderCommand> ClayVideoDemo_CreateLayout(VideoDemoData data, int yOffset) {
        var documents = data.documents;
        ClayFFM.Clay_BeginLayout();

        Function<Sizing, Sizing> layoutExpand = (s)->s
                .width(Sizing.grow(0))
                .height(Sizing.grow(0));
// todo: fix
        final Color contentBackgroundColor = Color.from(Color.scoped(90, 90, 90, 255));

        // Build UI here
        clay(id("OuterContainer")
                .backgroundColor(43, 41, 51, 255)
                .layout(l->l
                        .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                        .sizing(layoutExpand)
                        .padding(Clay.CLAY_PADDING_ALL(16))
                        .childGap(16)
                ), ()->{
            // Child elements go inside braces
            clay(id("HeaderBar")
                    .layout(l->l
                            .sizing(s->s
                                    .height(Sizing.fixed(60))
                                    .width(Sizing.grow(0))
                            )
                            .padding(16, 16, 0, 0)
                            .childGap(16)
                            .childAlignment(a->a
                                    .y(LayoutAlignmentY.ALIGN_Y_CENTER))
                    )
                    .backgroundColor(contentBackgroundColor)
                    .cornerRadius(Clay.CLAY_CORNER_RADIUS(8)), ()->{
                // Header buttons go here
                clay(id("FileButton")
                        .layout(l->l.padding(16, 16, 8, 8))
                        .backgroundColor(140, 140, 140, 255)
                        .cornerRadius(Clay.CLAY_CORNER_RADIUS(5)), ()->{
                    clayText(ClayString.literal("File"), cfg->cfg
                            .fontId(FONT_ID_BODY_16)
                            .fontSize(16)
                            .textColor(255, 255, 255, 255)
                    );
                    boolean fileMenuVisible =
                            ClayFFM.Clay_PointerOver(Clay.getElementId("FileButton"))
                                    ||
                                    ClayFFM.Clay_PointerOver(Clay.getElementId("FileMenu"));

                    if (fileMenuVisible) { // Below has been changed slightly to fix the small bug where the menu would dismiss when mousing over the top gap
                        clay(id("FileMenu")
                                .floating(f->f
                                        .attachTo(FloatingAttachToElement.ATTACH_TO_PARENT)
                                        .attachPoints(a->a
                                                .parent(FloatingAttachPointType.ATTACH_POINT_LEFT_BOTTOM)
                                        )
                                )
                                .layout(l->l
                                        .padding(0, 0, 8, 8)
                                ), ()->{
                            clay(id()
                                    .layout(l->l
                                            .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                            .sizing(s->s
                                                    .width(Clay.CLAY_SIZING_FIXED(200))
                                            )
                                    )
                                    .backgroundColor(40, 40, 40, 255)
                                    .cornerRadius(Clay.CLAY_CORNER_RADIUS(8)), ()->{
                                // Render dropdown items here
                                RenderDropdownMenuItem("New");
                                RenderDropdownMenuItem("Open");
                                RenderDropdownMenuItem("Close");
                            });
                        });// filemenu
                    }
                }); // end file button
                RenderHeaderButton("Edit");
                clay(id().layout(l->l.sizing(Clay.CLAY_SIZING_GROW(0))));
                RenderHeaderButton("Upload");
                RenderHeaderButton("Media");
                RenderHeaderButton("Support");
            }); // end header bar

            clay(id("LowerContent")
                    .layout(l->l.sizing(layoutExpand).childGap(16)), ()->{
                clay(id("Sidebar")
                        .backgroundColor(contentBackgroundColor)
                        .layout(l->l
                                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                .padding(Clay.CLAY_PADDING_ALL(16))
                                .childGap(8)
                                .sizing(s->s
                                        .width(Clay.CLAY_SIZING_FIXED(250))
                                        .height(Clay.CLAY_SIZING_GROW(0))
                                )), ()->{
                    Function<LayoutConfig, LayoutConfig> sidebarButtonLayout = l->l
                            .sizing(s->s.width(Clay.CLAY_SIZING_GROW(0)))
                            .padding(Clay.CLAY_PADDING_ALL(16));
                    for (int i = 0; i < documents.size(); i++) {
                        var document = documents.get(i);
                        if (i == data.selectedDocumentIndex) {
                            clay(id().layout(sidebarButtonLayout)
                                    .backgroundColor(120, 120, 120, 255)
                                    .cornerRadius(8), ()->{

                                clayText(document.title, cfg->cfg
                                        .fontId(FONT_ID_BODY_16)
                                        .fontSize(20)
                                        .textColor(255, 255, 255, 255));
                            });
                        } else {
                            final int hoverIndex = i;
                            clay(id().layout(sidebarButtonLayout)
                                    .backgroundColor(120, 120, 120, ClayFFM.Clay_Hovered() ? 120 : 0)
                                    .cornerRadius(Clay.CLAY_CORNER_RADIUS(8)), ()->{
                                Clay.onHover((_, p)->{
                                    if (p.pressedThisFrame()) {
                                        data.selectedDocumentIndex = hoverIndex;
                                    }
                                });
                                clayText(document.title, c->c
                                        .fontId(FONT_ID_BODY_16)
                                        .fontSize(20)
                                        .textColor(255, 255, 255, 255));
                            });
                        }
                    }
                }); // end sidebar
                clay(id("MainContent")
                        .backgroundColor(contentBackgroundColor)
                        .clip(c->c
                                .vertical(true)
                                .childOffset(getScrollOffset()))
                        .layout(l->l
                                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                .childGap(16)
                                .padding(p->p.all(16))
                                .sizing(layoutExpand)), ()->{

                    var selectedDocument = documents.get(data.selectedDocumentIndex);
                    clayText(selectedDocument.title, c->c
                            .fontId(FONT_ID_BODY_16)
                            .fontSize(24)
                            .textColor(COLOR_WHITE));
                    clayText(selectedDocument.text, c->c
                            .fontId(FONT_ID_BODY_16)
                            .fontSize(24)
                            .textColor(COLOR_WHITE));
                }); // end maincontent
            }); // end lowercontent
        }); // end outercontainer

        var renderCommands = Clay.endLayout();
        if (yOffset != 0) {
            // fixup Y offset for all commands for context 2
            for (var cmd : renderCommands) {
                var bb = new BoundingBox(Clay_RenderCommand.boundingBox(cmd.ms()));
                bb.y(bb.y() + yOffset);
            }
        }
        return renderCommands;
    }

}