package me.hienngo.astrodemo.domain.interactor;

import android.content.Context;
import android.content.SharedPreferences;

import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.hienngo.astrodemo.model.Channel;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author hienngo
 * @since 9/22/17
 */

public class BookmarkManager {
    private final Gson gson;
    private final Context context;
    private final BehaviorSubject<List<Channel>> subject = BehaviorSubject.create();

    public BookmarkManager(Gson gson, Context context) {
        this.gson = gson;
        this.context = context;

        subject.onNext(getSavedData());
    }

    public Observable<List<Channel>> getBookmarkedChannel() {
        return subject.asObservable();
    }

    public void add(Channel channel) {
        subject.asObservable().single().subscribe(list -> {
            if (!list.contains(channel)) {
                list.add(channel);
                saveData(list);
                subject.onNext(list);
            }
        });
    }

    public void remove(Channel channel) {
        subject.asObservable().single().subscribe(list -> {
            if (list.contains(channel)) {
                list.remove(channel);
                saveData(list);
                subject.onNext(list);
            }
        });
    }

    private void saveData(List<Channel> data) {
        getPreferences().edit().putString("bookmark", gson.toJson(data)).apply();
    }

    private List<Channel> getSavedData() {
        String value = getPreferences().getString("data", null);
        if (value != null) {
            Type listType = new TypeToken<List<Channel>>(){}.getType();
            return gson.fromJson(value, listType);
        } else {
            return new ArrayList<>();
        }
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences("bookmark", Context.MODE_PRIVATE);
    }

    public void sync(List<Channel> bookmarkedList) {
        Stream.of(bookmarkedList).forEach(this::add);
    }
}
