package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import javafx.scene.control.ListCell;

public class PeerListViewCell extends ListCell<Peer>
{
    @Override
    public void updateItem(Peer item, boolean empty)
    {
        super.updateItem(item,empty);
        if(item != null)
        {
            PeerListCellItem data = new PeerListCellItem();
            data.setInfo(item);
            setGraphic(data.getBox());
        }
    }
}