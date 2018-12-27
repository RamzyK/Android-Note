package fr.freekit.androidmvvm.sync;

import fr.freekit.androidmvvm.bdd.repos.LocalNoteRepository;
import fr.freekit.androidmvvm.rest.repos.RemoteNoteRepository;

public class SyncNote {

    LocalNoteRepository localNoteRepository;
    RemoteNoteRepository remoteNoteRepository;

    public SyncNote() {
        this.localNoteRepository = new LocalNoteRepository();
        this.remoteNoteRepository = new RemoteNoteRepository();
    }


}
