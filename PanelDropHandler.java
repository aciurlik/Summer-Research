import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.Transferable;
import javax.swing.TransferHandler;

/**
 * Blurb written: Long before 7/1/2017
 * Last updated: 7/26/2017
 * 
 * PanelDropHandler and ComponentDragHandler work together. They
 * make simple drag and drop operations easier to write by 
 * creating default behavior for the TransferHandler class.
 * 
 * PanelDropHandler can accept drops, while ComponentDragHandler 
 *   makes drops.
 *   
 * PanelDropHandler must specify what happens to the receiving container
 *   when a drop occurs, and ComponentDragHandler must specify what happens to
 *   the moving component and the donating container.
 * 
 * To use PanelDropHandler, you should create a subclass handler.
 *     Your subclass should override the method 
 *         public void recievedDrop(Container receiver, Component draggedItem)
 *     This method will say what your panel (the receiver) should do when
 *         a dragged component is dropped on it.
 * 
 * Then, to give a panel the specified drop behavior, just call the method
 *   myPanel.setTransferHandler(mySubclassOfPanelDropHandler)
 *   
 * Here's an example:
 * 
 * class labelRecieverDropHandler extends PanelDropHandler{
 *   @Override
 *   public void recievedDrop(Container receiver, Component draggedItem){
 *     if (draggedItem is a label){
 *       print the label's text.
 *     }
 *   }
 * }
 * 
 * JPanel p = new JPanel();
 * p.setTransferHandler(new labelRecieverDropHandler());
 * 
 * This JPanel will now receive any drops created by a ComponentDragHandler, 
 *   and if the drop was a label then it will print the label's text.
 * 
 * 
 * @author dannyrivers
 *
 */
public abstract class PanelDropHandler extends TransferHandler {

	@Override
	public boolean canImport(TransferSupport support)
	{
		if (!support.isDrop()){
			return false;
		}
		if(!support.isDataFlavorSupported(ComponentDragHandler.COMPONENT_FLAVOR)){
			return false;
		}
		return true;
	}

	/**
	 * The actions that should happen when a draggable component
	 * is dragged to this location.
	 * @param reciever
	 * @param draggedItem
	 */
	public abstract void recievedDrop(Container receiver, Component draggedItem);

	/**
	 * Normally, this method would call importData(component, transferable) 
	 *   which would import the data from transferable to the component.
	 * Here, however, we use TransferSupport because it can access both of those things.
	 * 
	 */
	@Override
	public boolean importData(TransferSupport support){
		if (!canImport(support)){
			return false;
		}
		Component dataComponent;
		try{
			Transferable t = support.getTransferable();
			Object rawData = t.getTransferData(ComponentDragHandler.COMPONENT_FLAVOR);
			dataComponent = (Component)rawData;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
		Container container = (Container)support.getComponent();
		recievedDrop(container, dataComponent);
		return true;
	}
	
	
}
