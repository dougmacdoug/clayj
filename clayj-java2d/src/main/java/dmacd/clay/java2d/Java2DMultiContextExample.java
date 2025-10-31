package dmacd.clay.java2d;

import dmacd.clay.Clay;
import dmacd.clay.java2d.util.DemoDocument;
import dmacd.clay.java2d.util.VideoDemoData;
import dmacd.ffm.clay.ClayFFM;
import dmacd.ffm.clay.Clay_ErrorHandler;
import dmacd.ffm.clay.Clay_SetMeasureTextFunction$measureTextFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static dmacd.clay.Clay.*;

public class Java2DMultiContextExample {

    static void main() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        var window = new JFrame();
        var gamePanel = new GamePanel();
        window.setTitle("Clay Demo Rendered in Swing");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().add(gamePanel, BorderLayout.CENTER);
        window.setLocationRelativeTo(null);
        window.pack();
        // run setup on the EDT so that Clay data is available to paint thread
        SwingUtilities.invokeLater(gamePanel::setup);
        window.setVisible(true);
        long period = Math.round((double)1_000_000_000/60.0); // 60 FPS
        executor.scheduleAtFixedRate(gamePanel, 0, period, TimeUnit.NANOSECONDS);
    }

    public static Font CustomFont(String path) {
        Font customFont = loadFont(path, 24f);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(customFont);
        return customFont;

    }
    public static Font loadFont(String path, float size){
        try {
            Font myFont = Font.createFont(Font.TRUETYPE_FONT, Java2DMultiContextExample.class.getResourceAsStream(path));
            return myFont.deriveFont(Font.PLAIN, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    static class GamePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, Runnable {
        private static final int FONT_ID_BODY_16 = 0;
        private static final Clay.Color CLAY_WHITE = globalClayColor(255, 255, 255, 255);
        int frameCount = 0;
        int mouseX = 100;
        int mouseY = 100;
        float mouseScroll = 0;
        boolean pressed = false;
        Thread gameThread;

        {
            setDoubleBuffered(true);
            setPreferredSize(new Dimension(1024, 768));
            setBackground(Color.black);
            addMouseMotionListener(this);
            addMouseListener(this);
            addMouseWheelListener(this);
        }

        void startGame() {
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public void mouseClicked(MouseEvent e) {  }

        @Override
        public void mousePressed(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                pressed = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                pressed = false;
            }
        }
        @Override
        public void mouseEntered(MouseEvent e) { }
        @Override
        public void mouseExited(MouseEvent e) { }
        @Override
        public void mouseDragged(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            mouseScroll = (float) e.getPreciseWheelRotation();
        }
        private Arena arena  = null;
        public void setup() {
            arena = Java2DRenderer.swingInitialize(1024, 768, "Java2D Clay Demo", 0);
            Java2DRenderer.theFont = Java2DMultiContextExample.CustomFont("/Roboto-Regular.ttf");
            Java2DRenderer.graphics =  getGraphics();
            Java2DRenderer.fontMetrics = Java2DRenderer.graphics.getFontMetrics(Java2DRenderer.theFont);

            long clayRequiredMemory = ClayFFM.Clay_MinMemorySize();
            var clayMemoryTop = ClayFFM.Clay_CreateArenaWithCapacityAndMemory(arena, clayRequiredMemory,
                    arena.allocate(clayRequiredMemory));
            var dims = Clay.Dimensions.of(getWidth(), (float) (getHeight() / 2));
            var errHandler = Clay_ErrorHandler.allocate(arena);
            var errFunc = Clay_ErrorHandler.errorHandlerFunction.allocate(Clay::errorHandler, arena);
            Clay_ErrorHandler.errorHandlerFunction(errHandler, errFunc);
            clayContextTop = ClayFFM.Clay_Initialize(clayMemoryTop, dims, errHandler); // This final argument is new since the video was published

            dataTop = ClayVideoDemo_Initialize();

            var measureTextFunc = Clay_SetMeasureTextFunction$measureTextFunction.allocate(Java2DRenderer::measureText, arena);
            ClayFFM.Clay_SetMeasureTextFunction(measureTextFunc, MemorySegment.NULL);

            var clayMemoryBottom = ClayFFM.Clay_CreateArenaWithCapacityAndMemory(arena, clayRequiredMemory, arena.allocate(clayRequiredMemory));
            // todo: in practice, possibly better to clone dims
            clayContextBottom = ClayFFM.Clay_Initialize(clayMemoryBottom, dims, errHandler); // This final argument is new since the video was published
            dataBottom = ClayVideoDemo_Initialize();
            ClayFFM.Clay_SetMeasureTextFunction(measureTextFunc, MemorySegment.NULL);
        }

        @Override
        public void run() {
            update();
            repaint();
        }
        long lastTime = System.nanoTime();
        long timer = 0;


        List<RenderCommand> renderCommandsTop;
        List<RenderCommand> renderCommandsBottom;
        public void update() {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        Clay.beginRenderLoop();
                        renderCommandsTop = CreateLayout(clayContextTop, dataTop, 0); //Clay_RenderCommandArray
                        renderCommandsBottom = CreateLayout(clayContextBottom, dataBottom, getHeight() / 2);
                    }
                });
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
            // TODO: any other updates
            long currentTime = System.nanoTime();
            timer += (currentTime - lastTime);
            lastTime = currentTime;
            if(timer >= 1_000_000_000) {
                System.out.println("FPS: " + frameCount);
                frameCount = 0;
                timer -= 1_000_000_000;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D gfx = (Graphics2D) g;
            if(renderCommandsTop !=null) {
                Java2DRenderer.swingRender(renderCommandsTop, gfx);
                Java2DRenderer.swingRender(renderCommandsBottom, gfx);
            }
            gfx.dispose();
            frameCount++;
            // best place to reset mouse scroll
            mouseScroll = 0;
        }
        MemorySegment clayContextTop;
        MemorySegment clayContextBottom;
        VideoDemoData dataTop;
        VideoDemoData dataBottom;

        List<RenderCommand> CreateLayout(MemorySegment context, VideoDemoData data, int yOffset) {
            ClayFFM.Clay_SetCurrentContext(context);
            ClayFFM.Clay_SetDebugModeEnabled(true);

            ClayFFM.Clay_SetLayoutDimensions(Clay.Dimensions.of(getWidth(), (float) (getHeight() / 2)));

            ClayFFM.Clay_SetPointerState(Clay.Vector2.scoped(mouseX, mouseY - yOffset).ms(), pressed);
            var scrollDelta = Vector2.scoped(0, mouseScroll);
            // todo: get fps from panel instead of hardcoded here
        ClayFFM.Clay_UpdateScrollContainers(true,
                scrollDelta.ms(), 1/60f);
            return ClayVideoDemo_CreateLayout(data, yOffset);
        }
        List<RenderCommand> ClayVideoDemo_CreateLayout(VideoDemoData data, int yOffset) {
            var documents = data.documents;
            ClayFFM.Clay_BeginLayout();

            Function<Clay.LayoutConfig.Sizing, Clay.LayoutConfig.Sizing> layoutExpand = (s)->s
                    .width(Clay.LayoutConfig.Sizing.grow(0))
                    .height(Clay.LayoutConfig.Sizing.grow(0));
// todo: fix
            final Clay.Color contentBackgroundColor = Clay.Color.scoped(90, 90, 90, 255);

            // Build UI here
            clay(id("OuterContainer")
                    .backgroundColor(43, 41, 51, 255)
                    .layout(l->l
                            .layoutDirection(Clay.LayoutDirection.TOP_TO_BOTTOM)
                            .sizing(layoutExpand)
                            .padding(Clay.CLAY_PADDING_ALL(16))
                            .childGap(16)
                    ), ()->{
                // Child elements go inside braces
                clay(id("HeaderBar")
                        .layout(l->l
                                .sizing(s->s
                                        .height(Clay.LayoutConfig.Sizing.fixed(60))
                                        .width(Clay.LayoutConfig.Sizing.grow(0))
                                )
                                .padding(16, 16, 0, 0)
                                .childGap(16)
                                .childAlignment(a->a
                                        .y(Clay.LayoutAlignmentY.ALIGN_Y_CENTER))
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
                                .textColor(CLAY_WHITE));
                        clayText(selectedDocument.text, c->c
                                .fontId(FONT_ID_BODY_16)
                                .fontSize(24)
                                .textColor(CLAY_WHITE));
                    }); // end maincontent
                }); // end lowercontent
            }); // end outercontainer

            var renderCommands = Clay.endLayout();
            if (yOffset != 0) {
                // fixup Y offset for all commands for context 2
                for (var cmd : renderCommands) {
                    var bb = cmd.boundingBox();
                    bb.y(bb.y() + yOffset);
                }
            }
            return renderCommands;
        }
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
    }

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

}
