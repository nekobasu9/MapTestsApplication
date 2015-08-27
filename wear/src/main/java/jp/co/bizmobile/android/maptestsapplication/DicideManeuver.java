package jp.co.bizmobile.android.maptestsapplication;

/**
 * Created by shotaroyoshida on 2015/08/25.
 */
public class DicideManeuver {



    final String TURN_SHARP_LEFT = "turn-sharp-left";
    final String UTURN_RIGHT = "uturn-right";
    final String TURN_SLIGHT_RIGHT ="turn-slight-right";
    final String MERGE = "merge";
    final String ROUNDABOUT_LEFT = "roundabout-left";
    final String ROUNDABOUT_RIGHT = "roundabout-right";
    final String UTURN_LEFT = "uturn-left";
    final String TURN_SLIGHT_LEFT = "turn-slight-left";
    final String TURN_LEFT = "turn-left";
    final String RAMP_RIGHT = "ramp-right";
    final String TURN_RIGHT = "turn-right";
    final String FORK_RIGHT = "fork-right";
    final String STARIGHT = "straight";
    final String FORK_LEFT = "fork-left";
    final String FERRY_TRAIN = "ferry-train";
    final String TURN_SHARP_RIGHT = "turn-sharp-right";
    final String RAMP_LEFT = "ramp-left";
    final String FERRY = "ferry";
    final String KEEP_LEFT = "keep-left";
    final String KEEP_RIGHT = "keep-right";




    int  jadgeManeuver(String maneuver){
        int returndrawble = 0 ;
        switch (maneuver){
            case TURN_SHARP_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case UTURN_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case TURN_SLIGHT_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case MERGE :
                returndrawble = R.drawable.arrow012s;
                break;

            case ROUNDABOUT_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case ROUNDABOUT_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case UTURN_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case TURN_SLIGHT_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case TURN_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case RAMP_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case TURN_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case FORK_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case STARIGHT :
                returndrawble = R.drawable.arrow012s;
                break;

            case FORK_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case FERRY_TRAIN :
                returndrawble = R.drawable.arrow012s;
                break;

            case TURN_SHARP_RIGHT :
                returndrawble = R.drawable.arrow012r;
                break;

            case RAMP_LEFT :
                returndrawble = R.drawable.arrow012l;
                break;

            case FERRY :
                returndrawble = R.drawable.arrow012s;
                break;

            case KEEP_LEFT :
                returndrawble = R.drawable.arrow012s;
                break;

            case KEEP_RIGHT :
                returndrawble = R.drawable.arrow012s;
                break;

            default:
                returndrawble = R.drawable.arrow012s;
                break;

        }

        return returndrawble;
    }


}
