package com.zondy.mapgis.controls.skin;

import java.nio.ByteBuffer;

import com.zondy.mapgis.controls.SceneControlNative;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import com.zondy.mapgis.controls.SceneControl;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Created by Administrator on 2020/4/17.
 */
public class SceneControlSkin extends SkinBase<SceneControl> {
    private static final float OUTPUT_SCALE_X = 1.0f;
    private static final float OUTPUT_SCALE_Y = 1.0f;

    private final Attribution mAttribution;

    private final PixelFormat<ByteBuffer> mPixelFormat = PixelFormat.getByteBgraPreInstance();
    //private final WritablePixelFormat<IntBuffer> mFormatInt = PixelFormat.getIntArgbPreInstance();
    private ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(1);

    private final AtomicBoolean mDisposed = new AtomicBoolean(false);

    private SimpleDoubleProperty mAttributionTopProperty;

    private boolean mSizeInvalidated = true;

    private WritableImage mImage;
    private final ImageView mImageView = new ImageView();

    private PixelWriter mPixelWriter;

    private int mScreenScaledContentWidth = 0;
    private int mScreenScaledContentHeight = 0;

    private SceneControl mSceneControl;

    private boolean mPause = true;

    public SceneControlSkin(SceneControl sceneControl) {
        super(sceneControl);
        this.mSceneControl = sceneControl;

        getChildren().add(this.mImageView);
        this.mImageView.setManaged(false);

        this.mAttribution = new Attribution(sceneControl);
        this.mAttribution.setId("attribution");

        this.mAttribution.setManaged(false);
        getChildren().add(this.mAttribution);

        sceneControl.viewInsetsProperty().addListener(observable -> sceneControl.requestLayout());

        sceneControl.widthProperty().addListener(observable -> this.mSizeInvalidated = true);
        sceneControl.heightProperty().addListener(observable -> this.mSizeInvalidated = true);
        sceneControl.insetsProperty().addListener(observable -> this.mSizeInvalidated = true);
        TIMER.start();
    }

    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        mPause = true;
        if (this.mSizeInvalidated) {
            this.mImageView.resize(contentWidth, contentHeight);

            this.mImageView.setFitWidth(contentWidth);
            this.mImageView.setFitHeight(contentHeight);

            this.mScreenScaledContentWidth = (int) (contentWidth * OUTPUT_SCALE_X);
            this.mScreenScaledContentHeight = (int) (contentHeight * OUTPUT_SCALE_Y);

            if (this.mScreenScaledContentWidth > 0 && this.mScreenScaledContentHeight > 0) {
                this.mImage = new WritableImage(this.mScreenScaledContentWidth, this.mScreenScaledContentHeight);
                this.mPixelWriter = this.mImage.getPixelWriter();
                this.mByteBuffer = ByteBuffer.allocateDirect(this.mScreenScaledContentWidth * this.mScreenScaledContentHeight * 4);
                //this.mSceneControl.resize(this.mScreenScaledContentWidth, this.mScreenScaledContentHeight);
                SceneControlNative.jni_OnSize(this.mSceneControl.getNativeHandle(), this.mScreenScaledContentWidth, this.mScreenScaledContentHeight);
                //this.mSceneControl.hasValidSize();
                //this.mContext.resume();
                mPause = false;
            } else {
                //this.mContext.pause();
                mPause = true;
            }
            this.mSizeInvalidated = false;
        }
        layoutInArea(this.mImageView, contentX, contentY, contentWidth, contentHeight, 0.0D, HPos.LEFT, VPos.TOP);

        this.mAttribution.update(getContentAreaWithoutInsets(contentX, contentY, contentWidth, contentHeight));

        getNode().setClip(new Rectangle(contentX, contentY, contentWidth, contentHeight));
    }

    private final AnimationTimer TIMER = new AnimationTimer() {
        public void handle(long now) {
            drawRequested();
        }
    };

    public void drawRequested() {
        if (!mPause && this.mScreenScaledContentWidth > 0 && this.mScreenScaledContentHeight > 0) {
            SceneControlNative.jni_Draw(this.mSceneControl.getNativeHandle(), this.mScreenScaledContentWidth, this.mScreenScaledContentHeight, this.mByteBuffer);
            //IntBuffer intBuf = this.mByteBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
            this.mPixelWriter.setPixels(0, 0, this.mScreenScaledContentWidth, this.mScreenScaledContentHeight, this.mPixelFormat, this.mByteBuffer, this.mScreenScaledContentWidth * 4);
            //this.mPixelWriter.setPixels(0, 0, this.mScreenScaledContentWidth, this.mScreenScaledContentHeight, this.formatInt, intBuf, this.mScreenScaledContentWidth);
            this.mImageView.setImage(this.mImage);
        }
    }

    public ReadOnlyDoubleProperty attributionTopProperty() {
        if (this.mAttributionTopProperty == null) {
            this.mAttributionTopProperty = new SimpleDoubleProperty();
            updateAttributionTopProperty();

            this.mAttribution.visibleProperty().addListener(o -> updateAttributionTopProperty());
            this.mAttribution.heightProperty().addListener(o -> updateAttributionTopProperty());

            getSkinnable().heightProperty().addListener(o -> updateAttributionTopProperty());
            getSkinnable().insetsProperty().addListener(o -> updateAttributionTopProperty());
            getSkinnable().viewInsetsProperty().addListener(o -> updateAttributionTopProperty());
        }
        return this.mAttributionTopProperty;
    }

    private void updateAttributionTopProperty() {
        SceneControl geoView = getSkinnable();
        if (this.mAttribution.isVisible()) {
            this.mAttributionTopProperty.set(geoView.getHeight() - geoView.getInsets().getBottom() - ((Insets) geoView.viewInsetsProperty().get()).getBottom() - this.mAttribution.getHeight());
        } else {
            this.mAttributionTopProperty.set(geoView.getHeight() - geoView.getInsets().getBottom());
        }
    }

    Rectangle getContentAreaWithoutInsets(double contentX, double contentY, double contentWidth, double contentHeight) {
        Rectangle area;
        SceneControl geoView = getSkinnable();

        if (geoView.isViewInsetsValid()) {
            Insets viewInsets = geoView.viewInsetsProperty().get();
            contentX += viewInsets.getLeft();
            contentY += viewInsets.getTop();
            contentWidth -= viewInsets.getLeft() + viewInsets.getRight();
            contentHeight -= viewInsets.getTop() + viewInsets.getBottom();
            area = new Rectangle(contentX, contentY, contentWidth, contentHeight);
        } else {
            area = new Rectangle(contentX, contentY, 0.0D, 0.0D);
        }
        return area;
    }

    private final class Attribution extends BorderPane {
        private static final int PADDING_EDGE = 2;
        private static final int SPACING = 2;
        private static final int WHITESPACE = 6;
        private final Label mAttributionText = new Label();
        private final Label mPoweredByEsri = new Label("Powered by MapGIS");

        private Attribution(SceneControl geoView) {
            String attributionStyleClass = "scenecontrol-attribution";
            String labelStyleClass = "label";
            String attributionLabelStyleClass = "scenecontrol-attribution-label";

            getStyleClass().add("scenecontrol-attribution");

            this.mAttributionText.getStyleClass().add("scenecontrol-attribution-label");
            this.mPoweredByEsri.getStyleClass().add("scenecontrol-attribution-label");


            this.mAttributionText.getStyleClass().remove("label");
            this.mPoweredByEsri.getStyleClass().remove("label");

            setLeft(this.mAttributionText);
            this.mAttributionText.setPadding(new Insets(0.0D, 1.0D, 0.0D, 2.0D));
            this.mAttributionText.setWrapText(false);
            geoView.addAttributionTextChangedListener(event -> {
                if (!SceneControlSkin.this.mDisposed.get()) {
                    this.mAttributionText.setText(geoView.getAttributionText());
                    geoView.requestLayout();
                }
            });

            setRight(this.mPoweredByEsri);
            this.mPoweredByEsri.setPadding(new Insets(0.0D, 2.0D, 0.0D, 1.0D));
            this.mPoweredByEsri.setMinWidth(Double.NEGATIVE_INFINITY);
            this.mPoweredByEsri.setMaxWidth(Double.NEGATIVE_INFINITY);

            setMaxHeight(Double.NEGATIVE_INFINITY);
            this.mAttributionText.setMinHeight(Double.NEGATIVE_INFINITY);
            visibleProperty().bind(geoView.attributionTextVisibleProperty());
            setOnMouseClicked(mouseEvent -> {
                this.mAttributionText.setWrapText(!this.mAttributionText.isWrapText());
                geoView.requestLayout();
            });
        }

        public void update(Rectangle area) {
            setMinWidth(area.getWidth());


            setWidth(getMinWidth());


            this.mAttributionText.setMinWidth(Math.max(0.0D, getWidth() - this.mPoweredByEsri.getWidth() - 6.0D));
            this.mAttributionText.setMaxWidth(this.mAttributionText.getMinWidth());

            layoutInArea(this, area.getX(), area.getY(), area.getWidth(), area.getHeight(), -1.0D, HPos.CENTER, VPos.BOTTOM);

            setClip(new Rectangle(getWidth(), getHeight()));
        }

//        public String getUserAgentStylesheet() {
//            return Attribution.class.getResource("attribution.css").toExternalForm();
//        }
    }
}
