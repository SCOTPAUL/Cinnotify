package uk.co.paulcowie.cinnotify;

import android.content.Context;
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

    private AllowedNotificationAppManager allowedApps;
    private Context appContext;
    private Button notificationButton;
    private NotificationAccessDialogManager notificationAccessDialogManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        notificationAccessDialogManager = new NotificationAccessDialogManager(getActivity());
        appContext = getActivity().getApplicationContext();
        allowedApps = new AllowedNotificationAppManager(appContext);

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        notificationButton = (Button) view.findViewById(R.id.notify_button);
        final Notifier notifier = new Notifier(view.getContext());

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifier.sendNotification("Hello", "World");
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

            notificationButton.setEnabled(allowedApps.canSendNotification(appContext.getPackageName()));
        }
    }
}
