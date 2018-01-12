/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * http://jgui.imagero.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartg.swing.titlebar.plaf;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import com.smartg.res.Resource;
import com.smartg.res.ResourceReader;
import com.smartg.res.ResourceReaderFactory;
import com.smartg.swing.NullMarginButton;
import com.smartg.swing.icons.EmptyIcon;
import com.smartg.swing.titlebar.TitleBar;
import com.smartg.swing.titlebar.plaf.images.CloseImage;
import com.smartg.swing.titlebar.plaf.images.CloseImageOver;
import com.smartg.swing.titlebar.plaf.images.CloseImagePressed;
import com.smartg.swing.titlebar.plaf.images.DockImage;
import com.smartg.swing.titlebar.plaf.images.DockImageOver;
import com.smartg.swing.titlebar.plaf.images.DockImagePressed;
import com.smartg.swing.titlebar.plaf.images.MaxImage;
import com.smartg.swing.titlebar.plaf.images.MaxImageOver;
import com.smartg.swing.titlebar.plaf.images.MaxImagePressed;
import com.smartg.swing.titlebar.plaf.images.MinImage;
import com.smartg.swing.titlebar.plaf.images.MinImageOver;
import com.smartg.swing.titlebar.plaf.images.MinImagePressed;
import com.smartg.swing.titlebar.plaf.images.RestoreImage;
import com.smartg.swing.titlebar.plaf.images.RestoreImageOver;
import com.smartg.swing.titlebar.plaf.images.RestoreImagePressed;
import com.smartg.swing.titlebar.plaf.images.UndockImage;
import com.smartg.swing.titlebar.plaf.images.UndockImageOver;
import com.smartg.swing.titlebar.plaf.images.UndockImagePressed;

/**
 * @author Andrei Kouznetsov
 * Date: 04.12.2003
 * Time: 16:53:43
 */
public class TitleBarUI extends ComponentUI {

    private static TitleBarUI instance;

    public static TitleBarUI createUI() {
        if (instance == null) {
            instance = new TitleBarUI();
        }
        return instance;
    };

    protected Icon maxIcon, minIcon, restoreIcon, closeIcon, dockIcon, undockIcon, paletteCloseIcon, systemIcon;
    protected Icon maxIconOver, minIconOver, restoreIconOver, closeIconOver, dockIconOver, undockIconOver, paletteCloseIconOver, systemIconOver;
    protected Icon maxIconPressed, minIconPressed, restoreIconPressed, closeIconPressed, dockIconPressed, undockIconPressed, paletteCloseIconPressed, systemIconPressed;

    @Override
	public void installUI(JComponent c) {
        installDefaults((TitleBar) c);
    }

    @Override
	public void uninstallUI(JComponent c) {
        systemIcon = null;
    }

    protected void installDefaults(TitleBar tb) {
        createIcons();
        AbstractButton systemButton = tb.getSystemButton();
        if (systemButton != null) {
            Icon icon = getSystemIcon(tb);
            Icon picon = getPressedSystemIcon(tb);
            systemButton.setIcon(icon);
            systemButton.setPressedIcon(picon);
        }
        else {
            Icon icon = getSystemIcon(tb);
            Icon picon = getPressedSystemIcon(tb);
            systemButton = createSystemButton(tb, icon);
            systemButton.setPressedIcon(picon);
            tb.setSystemButton(systemButton);
        }

        AbstractButton maximizeButton = tb.getMaximizeButton();
        if (maximizeButton != null) {
            maximizeButton.setIcon(getMaxIcon());
            maximizeButton.setRolloverIcon(getMaxRolloverIcon());
            maximizeButton.setPressedIcon(getMaxPressedIcon());
        }
        else {
            maximizeButton = createButton(tb, getMaxIcon(), getMaxRolloverIcon(), getMaxPressedIcon(), TitleBar.MAXIMIZE);
            tb.setMaximizeButton(maximizeButton);
        }

        AbstractButton minimizeButton = tb.getMinimizeButton();
        if (minimizeButton != null) {
            minimizeButton.setIcon(getMinIcon());
            minimizeButton.setRolloverIcon(getMinRolloverIcon());
            minimizeButton.setPressedIcon(getMinPressedIcon());
        }
        else {
            minimizeButton = createButton(tb, getMinIcon(), getMinRolloverIcon(), getMinPressedIcon(), TitleBar.MINIMIZE);
            tb.setMinimizeButton(minimizeButton);
        }

        AbstractButton closeButton = tb.getCloseButton();
        if (closeButton != null) {
            closeButton.setIcon(getCloseIcon());
            closeButton.setRolloverIcon(getCloseRolloverIcon());
            closeButton.setPressedIcon(getClosePressedIcon());
        }
        else {
            closeButton = createButton(tb, getCloseIcon(), getCloseRolloverIcon(), getClosePressedIcon(), TitleBar.CLOSE);
            tb.setCloseButton(closeButton);
        }

        AbstractButton dockButton = tb.getDockButton();
        if (dockButton != null) {
            dockButton.setIcon(getDockIcon());
            dockButton.setRolloverIcon(getDockRolloverIcon());
            dockButton.setPressedIcon(getDockPressedIcon());
        }
        else {
            dockButton = createButton(tb, getDockIcon(), getDockRolloverIcon(), getDockPressedIcon(), TitleBar.DOCK);
            tb.setDockButton(dockButton);
        }

        AbstractButton undockButton = tb.getUndockButton();
        if (undockButton != null) {
            undockButton.setIcon(getUndockIcon());
            undockButton.setRolloverIcon(getUndockRolloverIcon());
            undockButton.setPressedIcon(getUndockPressedIcon());
        }
        else {
            undockButton = createButton(tb, getUndockIcon(), getUndockRolloverIcon(), getUndockPressedIcon(), TitleBar.UNDOCK);
            tb.setUndockButton(undockButton);
        }

        AbstractButton restoreButton = tb.getRestoreButton();
        if (restoreButton != null) {
            restoreButton.setIcon(getRestoreIcon());
            restoreButton.setRolloverIcon(getRestoreRolloverIcon());
            restoreButton.setPressedIcon(getRestorePressedIcon());
        }
        else {
            restoreButton = createButton(tb, getRestoreIcon(), getRestoreRolloverIcon(), getRestorePressedIcon(), TitleBar.RESTORE);
            tb.setRestoreButton(restoreButton);
        }

        tb.setGlueLabel(createGlueLabel(tb));
    }

    protected JLabel createGlueLabel(TitleBar tb) {
        return new JLabel();
    }

    protected AbstractButton createSystemButton(final TitleBar tb, Icon icon) {
        final JButton b = new NullMarginButton(icon);
        b.setContentAreaFilled(false);
        b.setFocusable(false);
        b.setRolloverEnabled(true);
        if (icon instanceof ImageIcon) {
            b.setToolTipText(((ImageIcon) icon).getDescription());
        }
        return b;
    }

    protected AbstractButton createButton(final TitleBar tb, Icon icon, Icon overIcon, Icon pressedIcon, final String command) {
        JButton b = new NullMarginButton(icon);
        b.setRolloverIcon(overIcon);
        b.setPressedIcon(pressedIcon);
        b.setContentAreaFilled(false);
        b.setFocusable(false);
        b.setRolloverEnabled(true);
        if (icon instanceof ImageIcon) {
            b.setToolTipText(((ImageIcon) icon).getDescription());
        }
        b.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), command);
                tb.processEvent(ae);
            }
        });
        return b;
    }

    protected void createIcons() {
        if (systemIcon == null) {
            systemIcon = UIManager.getIcon("InternalFrame.icons");
            if (systemIcon == null) {
                systemIcon = new EmptyIcon(16, 16);
            }
        }
    }

    private Icon createIcon(ResourceReader reader, Resource res, String description) {
        Image image = (Image) reader.create(res);
        return new ImageIcon(image, description);
    }

    public Icon getMaxIcon() {
        if (maxIcon == null) {
            Resource res = new MaxImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                maxIcon = createIcon(rdr, res, "Maximize");
            }
        }
        return maxIcon;
    }

    public Icon getMaxRolloverIcon() {
        if (maxIconOver == null) {
            Resource res = new MaxImageOver();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                maxIconOver = createIcon(rdr, res, "Maximize");
            }
        }
        return maxIconOver;
    }

    public Icon getMaxPressedIcon() {
        if (maxIconPressed == null) {
            Resource res = new MaxImagePressed();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                maxIconPressed = createIcon(rdr, res, "Maximize");
            }
        }
        return maxIconPressed;
    }

    public Icon getMinIcon() {
        if (minIcon == null) {
            Resource res = new MinImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                minIcon = createIcon(rdr, res, "Minimize");
            }
        }
        return minIcon;
    }

    public Icon getMinRolloverIcon() {
        if (minIconOver == null) {
            Resource res = new MinImageOver();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                minIconOver = createIcon(rdr, res, "Minimize");
            }
        }
        return minIconOver;
    }

    public Icon getMinPressedIcon() {
        if (minIconPressed == null) {
            Resource res = new MinImagePressed();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                minIconPressed = createIcon(rdr, res, "Minimize");
            }
        }
        return minIconPressed;
    }

    public Icon getCloseIcon() {
        if (closeIcon == null) {
            Resource res = new CloseImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                closeIcon = createIcon(rdr, res, "Close");
            }
        }
        return closeIcon;
    }

    public Icon getCloseRolloverIcon() {
        if (closeIconOver == null) {
            Resource res = new CloseImageOver();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                closeIconOver = createIcon(rdr, res, "Close");
            }
        }
        return closeIconOver;
    }

    public Icon getClosePressedIcon() {
        if (closeIconPressed == null) {
            Resource res = new CloseImagePressed();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                closeIconPressed = createIcon(rdr, res, "Close");
            }
        }
        return closeIconPressed;
    }

    public Icon getPaletteCloseIcon() {
        if (paletteCloseIcon == null) {
            Resource res = new CloseImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                paletteCloseIcon = createIcon(rdr, res, "Close");
            }
        }
        return paletteCloseIcon;
    }

    public Icon getSystemIcon(TitleBar tb) {
        return systemIcon;
    }

    public Icon getPressedSystemIcon(TitleBar tb) {
        return systemIcon;
    }

    public Icon getRestoreIcon() {
        if (restoreIcon == null) {
            Resource res = new RestoreImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                restoreIcon = createIcon(rdr, res, "Restore");
            }
        }
        return restoreIcon;
    }

    public Icon getRestoreRolloverIcon() {
        if (restoreIconOver == null) {
            Resource res = new RestoreImageOver();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                restoreIconOver = createIcon(rdr, res, "Restore");
            }
        }
        return restoreIconOver;
    }

    public Icon getRestorePressedIcon() {
        if (restoreIconPressed == null) {
            Resource res = new RestoreImagePressed();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                restoreIconPressed = createIcon(rdr, res, "Restore");
            }
        }
        return restoreIconPressed;
    }

    public Icon getDockIcon() {
        if (dockIcon == null) {
            Resource res = new DockImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                dockIcon = createIcon(rdr, res, "Dock");
            }
        }
        return dockIcon;
    }

    public Icon getDockRolloverIcon() {
        if (dockIconOver == null) {
            Resource res = new DockImageOver();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                dockIconOver = createIcon(rdr, res, "Dock");
            }
        }
        return dockIconOver;
    }

    public Icon getDockPressedIcon() {
        if (dockIconPressed == null) {
            Resource res = new DockImagePressed();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                dockIconPressed = createIcon(rdr, res, "Dock");
            }
        }
        return dockIconPressed;
    }

    public Icon getUndockIcon() {
        if (undockIcon == null) {
            Resource res = new UndockImage();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                undockIcon = createIcon(rdr, res, "Undock");
            }
        }
        return undockIcon;
    }

    public Icon getUndockRolloverIcon() {
        if (undockIconOver == null) {
            Resource res = new UndockImageOver();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                undockIconOver = createIcon(rdr, res, "Undock");
            }
        }
        return undockIconOver;
    }

    public Icon getUndockPressedIcon() {
        if (undockIconPressed == null) {
            Resource res = new UndockImagePressed();
            ResourceReader rdr = ResourceReaderFactory.get(res.getType(), 0);
            if (rdr != null) {
                undockIconPressed = createIcon(rdr, res, "Undock");
            }
        }
        return undockIconPressed;
    }
}
