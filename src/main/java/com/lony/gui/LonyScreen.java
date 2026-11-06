package com.lony.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LonyScreen extends Screen {

    // === BẢNG MÀU CHUẨN TỪ SCRIPT ROBLOX (ARGB HEX) ===
    private static final int COLOR_BG_DARK = 0xA60E1318;
    private static final int COLOR_HEADER_BG = 0xCC080B0E;
    private static final int COLOR_SIDEBAR_BG = 0x8C080B0E;
    private static final int COLOR_MINT_NEON = 0xFF00FFC3;
    private static final int COLOR_MINT_LINE = 0xBF00FFC3;
    private static final int COLOR_MINT_INNER = 0x9900FFC3;
    private static final int COLOR_INACTIVE_STROKE = 0xFF647873;
    private static final int COLOR_INACTIVE_ICON = 0xFF829691;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;

    private final int guiWidth = 435;
    private final int guiHeight = 270;
    private final int topBarHeight = 26;
    private final int sidebarWidth = 90;

    private int windowX = -1;
    private int windowY = -1;
    private boolean draggingWindow = false;
    private int dragOffsetX, dragOffsetY;

    private int floatBtnX = 18;
    private int floatBtnY = -1;
    private final int floatBtnSize = 42;
    private boolean draggingFloatBtn = false;
    private int floatDragOffsetX, floatDragOffsetY;
    private boolean floatBtnMoved = false;

    private boolean isMenuOpen = true;
    private long openTimeMs;

    private String currentTab = "Main";
    private final Map<String, List<ModuleToggle>> tabs = new LinkedHashMap<>();

    public LonyScreen() {
        super(Text.literal("Lony 1.0"));
        initTabs();
    }

    private void initTabs() {
        List<ModuleToggle> mainModules = new ArrayList<>();
        mainModules.add(new ModuleToggle("Sprint Tu Dong (AutoSprint)", true));
        mainModules.add(new ModuleToggle("Nhin Trong Dem (Fullbright)", false));
        mainModules.add(new ModuleToggle("Hien Thi FPS & Toa Do", true));
        mainModules.add(new ModuleToggle("Toi Uu Hoa Hat (Particle Culling)", false));
        tabs.put("Main", mainModules);

        List<ModuleToggle> visualModules = new ArrayList<>();
        visualModules.add(new ModuleToggle("Vien Khoi Quang (Block Outline)", true));
        visualModules.add(new ModuleToggle("Thanh Mau Thuc The (Health HUD)", false));
        visualModules.add(new ModuleToggle("Hieu Ung Mo Nen (Menu Blur)", true));
        tabs.put("Visuals", visualModules);

        List<ModuleToggle> miscModules = new ArrayList<>();
        miscModules.add(new ModuleToggle("Tu Dong Cau Ca (AutoFish)", false));
        miscModules.add(new ModuleToggle("Canh Bao Giap Hong (Armor HUD)", true));
        tabs.put("Misc", miscModules);
    }

    @Override
    protected void init() {
        super.init();
        if (windowX == -1 || windowY == -1) {
            windowX = (this.width - guiWidth) / 2;
            windowY = (this.height - guiHeight) / 2;
        }
        if (floatBtnY == -1) {
            floatBtnY = (int) (this.height * 0.45f);
        }
        openTimeMs = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isMenuOpen) {
            this.renderBackground(context, mouseX, mouseY, delta);
        }

        renderFloatingButton(context, mouseX, mouseY);

        if (isMenuOpen) {
            float progress = Math.min(1.0f, (System.currentTimeMillis() - openTimeMs) / 250.0f);
            float scale = easeOutBack(progress);

            context.getMatrices().push();
            float centerX = windowX + guiWidth / 2.0f;
            float centerY = windowY + guiHeight / 2.0f;
            context.getMatrices().translate(centerX, centerY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.getMatrices().translate(-centerX, -centerY, 0);

            renderMainFrame(context, mouseX, mouseY);

            context.getMatrices().pop();
        }
    }

    private void renderFloatingButton(DrawContext context, int mouseX, int mouseY) {
        context.fill(floatBtnX, floatBtnY, floatBtnX + floatBtnSize, floatBtnY + floatBtnSize, COLOR_BG_DARK);

        int outerStrokeColor = isMenuOpen ? COLOR_MINT_NEON : COLOR_INACTIVE_STROKE;
        drawBorder(context, floatBtnX, floatBtnY, floatBtnSize, floatBtnSize, outerStrokeColor);

        int innerStrokeColor = isMenuOpen ? COLOR_MINT_INNER : COLOR_INACTIVE_STROKE;
        drawBorder(context, floatBtnX + 3, floatBtnY + 3, floatBtnSize - 6, floatBtnSize - 6, innerStrokeColor);

        int iconColor = isMenuOpen ? COLOR_MINT_NEON : COLOR_INACTIVE_ICON;
        String doveSymbol = "L";
        int symW = this.textRenderer.getWidth(doveSymbol);
        context.drawText(this.textRenderer, doveSymbol,
                floatBtnX + (floatBtnSize - symW) / 2,
                floatBtnY + (floatBtnSize - 8) / 2,
                iconColor, true);
    }

    private void renderMainFrame(DrawContext context, int mouseX, int mouseY) {
        context.fill(windowX, windowY, windowX + guiWidth, windowY + guiHeight, COLOR_BG_DARK);
        context.fill(windowX, windowY, windowX + guiWidth, windowY + topBarHeight, COLOR_HEADER_BG);

        String title = "Lony 1.0";
        int titleWidth = this.textRenderer.getWidth(title);
        context.drawText(this.textRenderer, title,
                windowX + (guiWidth - titleWidth) / 2,
                windowY + (topBarHeight - 8) / 2,
                COLOR_TEXT_WHITE, true);

        context.fill(windowX, windowY + topBarHeight, windowX + guiWidth, windowY + topBarHeight + 1, COLOR_MINT_LINE);
        context.fill(windowX, windowY + topBarHeight + 1, windowX + sidebarWidth, windowY + guiHeight, COLOR_SIDEBAR_BG);
        context.fill(windowX + sidebarWidth, windowY + topBarHeight + 1, windowX + sidebarWidth + 1, windowY + guiHeight, COLOR_MINT_LINE);

        drawBorder(context, windowX, windowY, guiWidth, guiHeight, COLOR_MINT_NEON);

        int tabBtnWidth = 68;
        int tabBtnHeight = 22;
        int tabX = windowX + (sidebarWidth - tabBtnWidth) / 2;
        int tabY = windowY + topBarHeight + 14;

        for (String tabName : tabs.keySet()) {
            boolean isSelected = tabName.equals(currentTab);
            if (isSelected) {
                context.fill(tabX, tabY, tabX + tabBtnWidth, tabY + tabBtnHeight, COLOR_MINT_NEON);
            }
            int textW = this.textRenderer.getWidth(tabName);
            context.drawText(this.textRenderer, tabName,
                    tabX + (tabBtnWidth - textW) / 2,
                    tabY + (tabBtnHeight - 8) / 2,
                    COLOR_TEXT_WHITE, true);
            tabY += tabBtnHeight + 9;
        }

        int contentX = windowX + sidebarWidth + 12;
        int contentY = windowY + topBarHeight + 12;
        int contentWidth = guiWidth - sidebarWidth - 24;

        List<ModuleToggle> currentModules = tabs.get(currentTab);
        if (currentModules != null) {
            for (ModuleToggle mod : currentModules) {
                int itemHeight = 26;
                boolean hovered = mouseX >= contentX && mouseX <= contentX + contentWidth
                        && mouseY >= contentY && mouseY <= contentY + itemHeight;

                int itemBg = hovered ? 0xCC141C24 : COLOR_HEADER_BG;
                context.fill(contentX, contentY, contentX + contentWidth, contentY + itemHeight, itemBg);
                drawBorder(context, contentX, contentY, contentWidth, itemHeight, mod.enabled ? COLOR_MINT_LINE : 0x33FFFFFF);

                context.drawText(this.textRenderer, mod.name, contentX + 10, contentY + 9, COLOR_TEXT_WHITE, true);

                String stateText = mod.enabled ? "ON" : "OFF";
                int badgeW = 28;
                int badgeH = 14;
                int badgeX = contentX + contentWidth - badgeW - 8;
                int badgeY = contentY + (itemHeight - badgeH) / 2;

                context.fill(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, mod.enabled ? COLOR_MINT_NEON : 0x55333333);
                int stateW = this.textRenderer.getWidth(stateText);
                context.drawText(this.textRenderer, stateText,
                        badgeX + (badgeW - stateW) / 2,
                        badgeY + 3,
                        mod.enabled ? 0xFF080B0E : 0xFFAAAAAA, false);

                contentY += itemHeight + 7;
            }
        }
    }

    private void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float) Math.pow(t - 1.0f, 3) + c1 * (float) Math.pow(t - 1.0f, 2);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (mouseX >= floatBtnX && mouseX <= floatBtnX + floatBtnSize
                    && mouseY >= floatBtnY && mouseY <= floatBtnY + floatBtnSize) {
                draggingFloatBtn = true;
                floatBtnMoved = false;
                floatDragOffsetX = (int) mouseX - floatBtnX;
                floatDragOffsetY = (int) mouseY - floatBtnY;
                return true;
            }

            if (isMenuOpen) {
                if (mouseX >= windowX && mouseX <= windowX + guiWidth
                        && mouseY >= windowY && mouseY <= windowY + topBarHeight) {
                    draggingWindow = true;
                    dragOffsetX = (int) mouseX - windowX;
                    dragOffsetY = (int) mouseY - windowY;
                    return true;
                }

                int tabBtnWidth = 68;
                int tabBtnHeight = 22;
                int tabX = windowX + (sidebarWidth - tabBtnWidth) / 2;
                int tabY = windowY + topBarHeight + 14;

                for (String tabName : tabs.keySet()) {
                    if (mouseX >= tabX && mouseX <= tabX + tabBtnWidth
                            && mouseY >= tabY && mouseY <= tabY + tabBtnHeight) {
                        currentTab = tabName;
                        return true;
                    }
                    tabY += tabBtnHeight + 9;
                }

                int contentX = windowX + sidebarWidth + 12;
                int contentY = windowY + topBarHeight + 12;
                int contentWidth = guiWidth - sidebarWidth - 24;

                List<ModuleToggle> currentModules = tabs.get(currentTab);
                if (currentModules != null) {
                    for (ModuleToggle mod : currentModules) {
                        int itemHeight = 26;
                        if (mouseX >= contentX && mouseX <= contentX + contentWidth
                                && mouseY >= contentY && mouseY <= contentY + itemHeight) {
                            mod.enabled = !mod.enabled;
                            return true;
                        }
                        contentY += itemHeight + 7;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingWindow) {
            windowX = (int) mouseX - dragOffsetX;
            windowY = (int) mouseY - dragOffsetY;
            return true;
        }
        if (draggingFloatBtn) {
            if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1) {
                floatBtnMoved = true;
            }
            floatBtnX = (int) mouseX - floatDragOffsetX;
            floatBtnY = (int) mouseY - floatDragOffsetY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (draggingFloatBtn && !floatBtnMoved) {
                isMenuOpen = !isMenuOpen;
                if (isMenuOpen) {
                    openTimeMs = System.currentTimeMillis();
                }
            }
            draggingWindow = false;
            draggingFloatBtn = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static class ModuleToggle {
        String name;
        boolean enabled;

        ModuleToggle(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }
    }
}
