package me.coley.recaf.ui.docking;

import com.panemu.tiwulfx.control.dock.DetachableTabPane;
import com.panemu.tiwulfx.control.dock.DetachableTabPaneFactory;
import javafx.scene.control.TabPane;
import javafx.stage.WindowEvent;

/**
 * Default region factory implementation.
 * Registers callbacks to {@link DockingManager#onRegionClose(DockingRegion)}.
 *
 * @author Matt Coley
 */
public class RegionFactory extends DetachableTabPaneFactory {
	private final DockingManager manager;

	/**
	 * @param manager
	 * 		Associated manager.
	 */
	public RegionFactory(DockingManager manager) {
		this.manager = manager;
	}

	@Override
	protected DetachableTabPane create() {
		return createInternal();
	}

	@Override
	protected void init(DetachableTabPane tabPane) {
		DockingRegion region = (DockingRegion) tabPane;
		region.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		region.setDetachableTabPaneFactory(this);
		region.setOnRemove(pane -> {
			if (region.isCloseIfEmpty())
				getManager().onRegionClose(region);
		});
		region.sceneProperty().addListener((obS, oldScene, newScene) -> {
			if (newScene != null) {
				newScene.windowProperty().addListener((obW, oldWindow, newWindow) ->
						newWindow.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> manager.onRegionClose(region)));
			}
		});
		manager.onRegionCreate(region);
	}

	/**
	 * Primarily used by {@link DockingManager#createRegion()}.
	 *
	 * @return New docking region.
	 */
	public DockingRegion createAndInit() {
		DockingRegion region = createInternal();
		init(region);
		return region;
	}

	protected DockingRegion createInternal() {
		return new DockingRegion(manager);
	}

	protected DockingManager getManager() {
		return manager;
	}
}
