import java.awt.Component;
import java.awt.Container;

/**
 * This class specifies the actions that should happen when a
 * requirement is dropped into a semester panel.
 * 
 * This should be moved to be a private subclass of SemesterPanel.
 * @author dannyrivers
 *
 */
public class SemesterPanelDropHandler extends PanelDropHandler{

	@Override
	public void recievedDrop(Container receiver, Component draggedItem) {
		receiver = (SemesterPanel) receiver;
		draggedItem = (RequirementPanel) draggedItem;
		reciever.addReq(draggedItem.getRequirement());
	}

}
