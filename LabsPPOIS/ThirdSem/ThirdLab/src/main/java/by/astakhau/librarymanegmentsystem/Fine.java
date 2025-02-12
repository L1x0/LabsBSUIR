package by.astakhau.librarymanegmentsystem;

public class Fine {
    private int coast;

    public Fine(int coast, LibraryAccount libraryAccount) {
        this.coast = coast;
        libraryAccount.addNotifications(new Notification("fine",
                "you have fine (" + coast + " dollars)"));
    }
}
