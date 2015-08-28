package uk.co.paulcowie.cinnotify;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import uk.co.paulcowie.cinnotify.util.Notifier;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private NotificationAccessDialogManager notificationAccessDialogManager;

    public MainActivityFragment() {
        notificationAccessDialogManager = new NotificationAccessDialogManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        final Button notification_button = (Button) view.findViewById(R.id.notify_button);
        final Notifier notifier = new Notifier(view.getContext());

        notification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifier.sendNotification("Foo", "Bar");
            }
        });


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        View view = getView();
        if(view != null){
            notificationAccessDialogManager.setContext(view.getContext());
            notificationAccessDialogManager.popupIfAccessNeeded();
        }
    }
}
