package com.zondy.mapgis.controls.wizard;

import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * @author CR
 * @file WizardPage.java
 * @brief 向导页面
 * @create 2019-03-05，推荐使用Dialog，setDialogPane(wizard)
 */
public class WizardPage extends VBox
{
    //region 变量
    private StringProperty text = new SimpleStringProperty();
    private StringProperty descriptionText = new SimpleStringProperty();
    private BooleanProperty allowPrevious = new SimpleBooleanProperty(true);
    private BooleanProperty allowCancel = new SimpleBooleanProperty(true);
    private BooleanProperty allowNext = new SimpleBooleanProperty(true);
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private BooleanProperty helpVisible = new SimpleBooleanProperty(false);
    private ObjectProperty<Image> headerImage = new SimpleObjectProperty<>();
    private ObjectProperty<Node> header = new SimpleObjectProperty<>();
    private ObjectProperty<Node> content = new SimpleObjectProperty<>();
    private Wizard owner;

    public EventHandler<WizardEvent> getOnPageValidating()
    {
        return onPageValidating.get();
    }

    public ObjectProperty<EventHandler<WizardEvent>> onPageValidatingProperty()
    {
        return onPageValidating;
    }

    public void setOnPageValidating(EventHandler<WizardEvent> onPageValidating)
    {
        this.onPageValidating.set(onPageValidating);
    }

    private final ObjectProperty<EventHandler<WizardEvent>> onPageValidating = new ObjectPropertyBase<EventHandler<WizardEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(WizardEvent.PAGEVALIDATING, get());
        }

        @Override
        public Object getBean()
        {
            return this;
        }

        @Override
        public String getName()
        {
            return "onFinish";
        }
    };
    //endregion

    //region 构造函数
    public WizardPage()
    {
        this(null);
    }

    public WizardPage(Node content)
    {
        this(content, "");
    }

    public WizardPage(Node content, String text)
    {
        this(content, text, "");
    }

    public WizardPage(Node content, String text, String description)
    {
        this(content, text, description, null);
    }

    public WizardPage(Node content, String text, String description, Image headerImage)
    {
        this.setText(text);
        this.setDescriptionText(description);
        this.setHeaderImage(headerImage);
        this.setContent(content);
    }
    //endregion

    //region 属性get&set-Header
    public Node getHeader()
    {
        if (this.header.get() == null) {
            Label labelText = new Label(this.text.get());
            labelText.setFont(Font.font("SimHei", 14));//"Microsoft YaHei"
            Label labelDescription = new Label(this.descriptionText.get());
            VBox vBoxLabel = new VBox(2, labelText, labelDescription);
            vBoxLabel.setMargin(labelDescription, new Insets(0, 0, 0, 26));
            vBoxLabel.setPadding(new Insets(8, 0, 9, 0));

            HBox hBox = new HBox(vBoxLabel);
            hBox.setPadding(new Insets(2, 12, 2, 12));
            hBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("white"), null, null)));

            Wizard owner = this.getOwner();
            if (owner != null && owner.getShowHeaderImage()) {
                Image image = this.headerImage.get();
                if (image == null) {
                    image = (owner.getHeaderImage() != null) ? owner.getHeaderImage() : new Image(getClass().getResourceAsStream("wizardHeader_48.png"));
                }
                ImageView imageView = new ImageView(image);
                Region region = new Region();
                hBox.getChildren().addAll(region, imageView);
                HBox.setHgrow(region, Priority.ALWAYS);
            }

            VBox vBoxHeader = new VBox(hBox, new Separator());
            this.header.set(vBoxHeader);
        }
        return header.get();
    }

    public ObjectProperty<Node> headerProperty()
    {
        return header;
    }

    public void setHeader(Node pageHeader)
    {
        this.header.set(pageHeader);
    }

    public String getText()
    {
        return text.get();
    }

    public StringProperty textProperty()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text.set(text);
    }

    public String getDescriptionText()
    {
        return descriptionText.get();
    }

    public StringProperty descriptionTextProperty()
    {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText)
    {
        this.descriptionText.set(descriptionText);
    }

    public Image getHeaderImage()
    {
        return headerImage.get();
    }

    public ObjectProperty<Image> headerImageProperty()
    {
        return headerImage;
    }

    public void setHeaderImage(Image headerImage)
    {
        this.headerImage.set(headerImage);
    }
    //endregion

    // region 属性get&set-Content
    public Node getContent()
    {
        return content.get();
    }

    public ObjectProperty<Node> pageContentProperty()
    {
        return content;
    }

    public void setContent(Node content)
    {
        Region region = new Region();
        VBox vBox = new VBox(region, new Separator());
        if (content != null) {
            vBox.getChildren().add(0, content);
            vBox.setMargin(content, new Insets(10, 12, 12, 12));
        }
        VBox.setVgrow(region, Priority.ALWAYS);
        vBox.setPadding(new Insets(0));
        this.content.set(vBox);
    }
    //endregion

    // region 属性get&set-按钮
    public boolean getAllowPrevious()
    {
        return allowPrevious.get();
    }

    public BooleanProperty allowPreviousProperty()
    {
        return allowPrevious;
    }

    public void setAllowPrevious(boolean allowPrevious)
    {
        this.allowPrevious.set(allowPrevious);
    }

    public boolean getAllowCancel()
    {
        return allowCancel.get();
    }

    public BooleanProperty allowCancelProperty()
    {
        return allowCancel;
    }

    public void setAllowCancel(boolean allowCancel)
    {
        this.allowCancel.set(allowCancel);
    }

    public boolean getAllowNext()
    {
        return allowNext.get();
    }

    public BooleanProperty allowNextProperty()
    {
        return allowNext;
    }

    public void setAllowNext(boolean allowNext)
    {
        this.allowNext.set(allowNext);
    }

    public boolean getHelpVisible()
    {
        return helpVisible.get();
    }

    public BooleanProperty helpVisibleProperty()
    {
        return helpVisible;
    }

    public void setHelpVisible(boolean helpVisible)
    {
        this.helpVisible.set(helpVisible);
    }
    //endregion

    // region 属性get&set-其他
    public boolean isSelected()
    {
        boolean sel = false;
        Wizard owner = this.getOwner();
        if (owner != null) {
            sel = this.equals(owner.getSelectedPage());
        }
        this.selected.set(sel);
        return selected.get();
    }

    public BooleanProperty selectedProperty()
    {
        return selected;
    }

    /**
     * 需将WizardPage添加到Wizard中之后才能取到此值
     *
     * @return
     */
    public Wizard getOwner()
    {

        return owner;
    }

    /**
     * 无需调用。在Wizard中添加Page时自动设置
     *
     * @param owner
     */
    public void setOwner(Wizard owner)
    {
        //保证只设置一次，不允许用户乱设
        if (this.owner == null) {
            this.owner = owner;
        }
    }
    //endregion
}
