package net.wandroid.answer.view;

public interface IControllButtonListener {
    void onSaveEntry();

    void onSlideBack();

    void onNextSlide();

    void onCancel();

    static final IControllButtonListener NO_LISTENER = new IControllButtonListener() {

        @Override
        public void onSlideBack() {
        }

        @Override
        public void onSaveEntry() {
        }

        @Override
        public void onNextSlide() {
        }

        @Override
        public void onCancel() {
        }
    };
}
