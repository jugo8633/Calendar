//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.view;


import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationType {
    public AnimationType() {}

    public Animation inFromTopAnimation(int nDuration) {
        Animation inFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.02f);

        inFromBottom.setDuration(nDuration);
        inFromBottom.setInterpolator(new AccelerateInterpolator());

        return inFromBottom;
    }

    public Animation outToTopAnimation(int nDuration) {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.0f,
                                    Animation.RELATIVE_TO_PARENT, -1.0f);

        outToBottom.setDuration(nDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }

    public Animation inFromDownAnimation(int nDuration) {
        Animation inFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f);

        inFromBottom.setDuration(nDuration);
        inFromBottom.setInterpolator(new AccelerateInterpolator());

        return inFromBottom;
    }

    public Animation outToDownAnimation(int nDuration) {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.00f,
                                    Animation.RELATIVE_TO_PARENT, 1.0f);

        outToBottom.setDuration(nDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }

    public Animation inFromRightAnimation(int nDuration) {
        Animation inFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f);

        inFromBottom.setDuration(nDuration);
        inFromBottom.setInterpolator(new AccelerateInterpolator());

        return inFromBottom;
    }

    public Animation outToRightAnimation(int nDuration) {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f);

        outToBottom.setDuration(nDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }

    public Animation inFromLeftAnimation(int nDuration) {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f);

        outToBottom.setDuration(nDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }

    public Animation outToLeftAnimation(int nDuration) {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f);

        outToBottom.setDuration(nDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }
}
