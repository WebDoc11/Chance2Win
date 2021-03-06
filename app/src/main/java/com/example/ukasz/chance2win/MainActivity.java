package com.example.ukasz.chance2win;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private int[] handCards;
    private int[] tableCards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        //seekBar onChangeListener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView playersCount = (TextView) findViewById(R.id.playersCount);
                playersCount.setText(getResources().getString(R.string.playersCountLabel) + ": " + Integer.toString((progress+1)));

                SharedPreferences.Editor editor = getSharedPreferences(App.PREFS_NAME, 0).edit();
                editor.putInt("playersCount", progress+1);
                editor.commit();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SharedPreferences settings = getSharedPreferences(App.PREFS_NAME, 0);
        TextView playersCount = (TextView) findViewById(R.id.playersCount);

        int progress = settings.getInt("playersCount", 1);
        playersCount.setText(getResources().getString(R.string.playersCountLabel) + ": " + Integer.toString(progress));
        seekBar.setProgress(progress-1);

        if(App.currentClicked != null) {
            int selectedCount = settings.getInt("selectedCount", 0);
            boolean findedIndex = false;
            for(int i = 1; i <= selectedCount; i++) {
                if(App.currentClicked == settings.getInt("Btn"+i, 0)) {
                    findedIndex = true;
                    selectedCount = i;
                    break;
                }
            }

            SharedPreferences.Editor editor = settings.edit();

            if(findedIndex) {

                int previousResourceId = settings.getInt("Res"+selectedCount, 0);
                App.deck.addToDeck(App.deck.getCardByDrawable(previousResourceId));

            } else {
                selectedCount++;
                editor.putInt("selectedCount", selectedCount);

            }

            editor.putInt("Res" + selectedCount, App.currentResourceId);
            editor.putInt("Btn" + selectedCount, App.currentClicked);
            editor.commit();

            App.currentClicked = null;
        }

        int selectedCount = settings.getInt("selectedCount", 0);

        if(selectedCount > 0) {
            int cards[] = new int[selectedCount];
            handCards = new int[2];
            tableCards = new int[5];

            int handCardIndex = 0;
            int tableCardIndex = 0;

            for (int i = 1; i <= selectedCount; i++) {
                int currentBtn = settings.getInt("Btn" + i, 0);
                if (currentBtn != 0) {
                    int currentKey = settings.getInt("Res" + i, 0);

                    // cards to send to resultActivity
                    if(currentBtn != R.id.cardOnHand1 && currentBtn != R.id.cardOnHand2) {
                        tableCards[tableCardIndex] = currentKey;
                        tableCardIndex++;
                    } else {
                        handCards[handCardIndex] = currentKey;
                        handCardIndex++;
                    }


                    cards[i-1] = currentKey;
                    if (currentKey != 0) {
                        ImageButton btn = (ImageButton) findViewById(currentBtn);
                        Picasso.with(this).load(currentKey).resize(115, 140).into(btn);
                    }
                }
            }
            // jeśli wybrano wszystkie karty to wyświetlamy jaki układ ma user, w przeciwnym wypadku domyślną wartość
            TextView info = (TextView) findViewById(R.id.currentCardsInfo);
            if (selectedCount >= 5) {
                Poker poker = new Poker(cards);
                info.setText(poker.getHandType());

            } else {
                info.setText(getResources().getString(R.string.cardsInfoDefault));
            }
        }
    }

    public void selectCard(View view) {
        App.currentClicked = view.getId();
        Intent intent = new Intent(this, CardListActivity.class);
        startActivity(intent);
    }


    public void reset(View view) {
        SharedPreferences settings = getSharedPreferences(App.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();

        App.deck = new Deck();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void result(View view) {
        SharedPreferences settings = getSharedPreferences(App.PREFS_NAME, 0);
        int selectedCount = settings.getInt("selectedCount", 0);

        if(selectedCount == 7) {

            Intent intent = new Intent(this, ResultListActivity.class);

            intent.putExtra("handCards", handCards);
            intent.putExtra("tableCards", tableCards);
            startActivity(intent);

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.selectAllCardsWarning), Toast.LENGTH_SHORT).show();
        }

    }
}
