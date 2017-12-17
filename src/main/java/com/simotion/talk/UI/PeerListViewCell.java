package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import javafx.scene.control.ListCell;

// public class PeerListViewCell
// 피어 목록의 Custom ListCell
public class PeerListViewCell extends ListCell<Peer>
{
    @Override
    public void updateItem(Peer item, boolean empty)
    {
        super.updateItem(item,empty);
        if(item != null)
        {
            PeerListCellItemController data = new PeerListCellItemController();
            data.setInfo(item);
            setGraphic(data.getBox());
        }
    }
}