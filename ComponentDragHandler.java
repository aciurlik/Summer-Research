import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;


/**
 * Blurb written: Long before 7/1/2017
 * Last Updated: 7/26/2017
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
 *   
 *   
 *   
 *   
 * To use ComponentDragHandler, you should 
 * 	   create a subclass of ComponentDragHandler,
 * 	   have the component you want to drag call the method
 * 	       myComp.setTransferHandler(mySubclass);
 * 	   then register a mouseListener that converts clicks into drags.
 * 		   myComp.addMouseListener(ComponentDragHandler.getDragListener())
 * 
 * 		   
 * 
 *     Your subclass should override the methods:
 *         public void initiateDrag(JComponent toBeDragged)
 *         public void afterDrop(Container source, JComponent dragged, boolean moveAction)
 * 
 *     The former specifies actions that should happen as soon as a drag event starts.
 *     The latter specifies actions that should happen after the drag event has completed. 
 *         the argument 'dragged' may be null.
 *     if moveAction is true, then this drag was specified by the user to be a move action.
 *         Most implementations decide that on a move action, the dragged component should be
 *         removed from the source container, but this is up to you.
 * 
 * 
 * @author dannyrivers
 *
 */

public abstract class ComponentDragHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;
	
	private Image mouseImage;

	public static final DataFlavor COMPONENT_FLAVOR;
	static{
		try{
			COMPONENT_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Component.class.getName() + "\"");
		} catch(Exception e){
			throw new RuntimeException("failed to create COMPONENT_FLAVOR");
		}
	}

	public static MouseAdapter getDragListener(){
		return new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e)){
					return;
				}
				JComponent c = (JComponent) e.getSource();
				TransferHandler handler = c.getTransferHandler();
				handler.exportAsDrag(c, e, TransferHandler.MOVE);
			}
		};
	}
	
	public static MouseAdapter passingAdapter(){
		return new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				Component source = (Component) e.getSource();
				MouseEvent parentEvent = SwingUtilities.convertMouseEvent(source, e, source.getParent());
		        source.getParent().dispatchEvent(parentEvent);
			}
		};
	}


	//When a TransferHandler is created, it will ask for the possible source
	// actions. We don't want to mess with the constructors, so we will set the
	// mouse image in this method rather than in a constructor.
	@Override
	public int getSourceActions(JComponent c)
	{
		return TransferHandler.COPY_OR_MOVE;
	}


	/**
	 * The first call to getImage will set the drag image
	 * to be the paint of this component.
	 * After that, future calls return the first image.
	 * @param c
	 * @return
	 */
	public Image getImage(JComponent c){
		if(mouseImage != null){
			return mouseImage;
		}
		createImage(c);
		return mouseImage;
	}
	/**
	 * I actually wrote this one (danny rivers)
	 * It returns an image of the component 
	 * Background will be omitted, so make sure your component
	 * fills its bounds.
	 * @param c
	 * @return
	 */
	public void createImage(JComponent c){
		Dimension size = c.getSize();
		BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = myImage.createGraphics();
		c.paint(g2);
		this.mouseImage = myImage;
		setDragImage( this.mouseImage );
	}


	/**
	 * Make a Transferable that holds just this component.
	 * our component will be the only element of the list.
	 */
	@Override
	public Transferable createTransferable(final JComponent c)
	{
		initiateDrag(c);
		this.getImage(c);
		return new componentTransferable(c);
	}

	public class componentTransferable implements Transferable{
		private JComponent data;
		public componentTransferable(JComponent source){
			this.data = source; // the thing doing the packaging is also the stuff being packaged.
		}
		@Override
		public Object getTransferData(DataFlavor flavor)
		{
			return this.data;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			DataFlavor[] flavors = new DataFlavor[1];
			flavors[0] = COMPONENT_FLAVOR;
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return flavor.equals(COMPONENT_FLAVOR);
		}

	}

	@Override
	public void exportDone(JComponent source, Transferable data, int action)
	{
		JComponent dragged = null;
		try{
			Component holder = (Component) data.getTransferData(COMPONENT_FLAVOR);
			dragged = (JComponent) holder;
		} catch (Exception e){
			// Auto-generated catch block
			e.printStackTrace();
		}
		afterDrop(source, dragged, action == TransferHandler.MOVE);
	}







	/**
	 * Subclasses should override this method.
	 * Anything that needs to happen to start a drag should 
	 * be put in this method.
	 * 
	 */
	public abstract void initiateDrag(JComponent toBeDragged);
	/**
	 * Subclasses should override this method.
	 * Anything that needs to happen after a drop is complete
	 * should be put in this method.
	 * 
	 * moveAction will be true if a move was requested, so you should
	 * make sure the component's old container no longer contains it.
	 */
	public abstract void afterDrop(Container source, JComponent dragged, boolean moveAction);


}
