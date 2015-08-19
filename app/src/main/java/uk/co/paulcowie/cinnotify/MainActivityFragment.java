package uk.co.paulcowie.cinnotify;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.co.paulcowie.cinnotify.util.Notifier;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        new NotificationAccessDialogManager(view.getContext()).popupIfAccessNeeded();

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
}
