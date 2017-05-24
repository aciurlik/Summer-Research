

import javax.swing.TransferHandler;
import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler.DropLocation;
import javax.swing.TransferHandler.TransferSupport;

public abstract class PanelDropHandler extends TransferHandler {

	@Override
	public boolean canImport(TransferSupport support)
	{
		if (!support.isDrop())
		{
			return false;
		}

		boolean canImport = support.isDataFlavorSupported(ComponentDragHandler.COMPONENT_FLAVOR);
		return canImport;
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
		if (!canImport(support))
		{
			return false;
		}

		Component dataComponent;

		try
		{
			Transferable t = support.getTransferable();
			Object rawData = t.getTransferData(ComponentDragHandler.COMPONENT_FLAVOR);
			dataComponent = (Component)rawData;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		Container container = (Container)support.getComponent();
		container.add(dataComponent);
		//      container.revalidate();
		//      container.repaint();
		container.invalidate();
		//container.getParent().revalidate();
		//container.getParent().repaint();

		//      JLabel label = (JLabel)component;
		DropLocation location = support.getDropLocation();
		//      System.out.println(label.getText() + " + " + location.getDropPoint());
		dataComponent.setLocation( location.getDropPoint() );
		
		recievedDrop(container, dataComponent);

		return true;
	}

}
