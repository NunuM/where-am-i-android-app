package me.nunum.whereami.framework;

import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;

/**
 * Created by nuno on 10-11-2017.
 */

public interface StreamFlow {

    enum STREAM_STATE {

        PAUSE {
            @Override
            public String toString() {
                return "Pause";
            }
        },
        RESUME {
            @Override
            public String toString() {
                return "Resume";
            }
        },
        STOP {
            @Override
            public String toString() {
                return "Stop";
            }
        },
        RUNNING {
            @Override
            public String toString() {
                return "Running";
            }
        }

    }


    enum FLUSH_MODE {
        TCP,
        DATABASE,
        TCP_AND_DB_DRAIN,
        NONE
    }


    boolean start(Localization localization,
                  Position position,
                  OnSample onSampleCallback,
                  Cache cache);



    boolean stop();

    /**
     * @return
     */
    STREAM_STATE currentState();

    /**
     * @return
     */
    FLUSH_MODE currentFlushMode();
}
