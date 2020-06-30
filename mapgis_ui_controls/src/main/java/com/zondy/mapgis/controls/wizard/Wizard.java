package com.zondy.mapgis.controls.wizard;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file Wizard.java
 * @brief 向导控件。推荐使用Dialog，setDialogPane(wizard)
 * @create 2019-03-05
 */
public class Wizard extends DialogPane
{
    //region 变量
    private StringProperty helpText = new SimpleStringProperty("帮助");
    private StringProperty previousText = new SimpleStringProperty("< 上一步");
    private StringProperty nextText = new SimpleStringProperty("下一步 >");
    private StringProperty finishText = new SimpleStringProperty("完成");
    private StringProperty cancelText = new SimpleStringProperty("取消");
    private BooleanProperty helpVisible = new SimpleBooleanProperty(false);
    private BooleanProperty cancelVisible = new SimpleBooleanProperty(true);
    private BooleanProperty showHeader = new SimpleBooleanProperty(true);
    private BooleanProperty showHeaderImage = new SimpleBooleanProperty(true);
    private ObjectProperty<Image> headerImage = new SimpleObjectProperty<>();
    private IntegerProperty selectedPageIndex = new SimpleIntegerProperty(-1);
    private ObjectProperty<WizardPage> selectedPage = new SimpleObjectProperty<>();
    private ObservableList<WizardPage> pages = FXCollections.observableArrayList();
    private ButtonType buttonTypeHelp = new ButtonType(this.helpText.get(), ButtonBar.ButtonData.HELP_2);

    //endregion

    /**
     * 构造向导控件
     *
     * @param ownerDialog 所属对话框，用于添加Shown事件在其中选中第一个页面，在构造函数里面显示会导致有些设置（如showHeader）没有效果.传null时需用户自己初始选中第一页。
     * @param wizardPages 页面集合
     */
    public Wizard(Dialog ownerDialog, WizardPage... wizardPages)
    {
        this.pages.addListener(new ListChangeListener<WizardPage>()
        {
            @Override
            public void onChanged(Change<? extends WizardPage> c)
            {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (WizardPage page : c.getAddedSubList()) {
                            page.setOwner(Wizard.this);
                        }
                    }
                    if (c.wasRemoved()) {
                        for (WizardPage page : c.getRemoved()) {
                            page.setOwner(null);
                        }
                    }

                    if (getButtonTypes().contains(ButtonType.NEXT)) {
                        ((Button) lookupButton(ButtonType.NEXT)).visibleProperty().bind(selectedPageIndex.lessThan(pages.size() - 1));
                    }
                    if (getButtonTypes().contains(ButtonType.FINISH)) {
                        (lookupButton(ButtonType.FINISH)).visibleProperty().bind(selectedPageIndex.isEqualTo(pages.size() - 1));
                    }
                }
            }
        });

        this.pages.addAll(wizardPages);

        //region 按钮文本和可见性
        this.getButtonTypes().addListener(new ListChangeListener<ButtonType>()
        {
            @Override
            public void onChanged(Change<? extends ButtonType> c)
            {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (ButtonType type : c.getAddedSubList()) {
                            Button button = (Button) lookupButton(type);
                            if (type.equals(buttonTypeHelp)) {
                                button.textProperty().bind(helpText);
                                button.addEventFilter(ActionEvent.ACTION, event -> {
                                    event.consume();
                                    fireEvent(new WizardEvent(WizardEvent.HELP));
                                });
                            } else if (type.equals(ButtonType.PREVIOUS)) {
                                button.textProperty().bind(previousText);
                                button.addEventFilter(ActionEvent.ACTION, event -> {
                                    event.consume();
                                    setSelectedPageIndex(selectedPageIndex.get() - 1);
                                });
                            } else if (type.equals(ButtonType.NEXT)) {
                                button.textProperty().bind(nextText);
                                button.addEventFilter(ActionEvent.ACTION, event -> {
                                    event.consume();
                                    setSelectedPageIndex(selectedPageIndex.get() + 1);
                                });
                            } else if (type.equals(ButtonType.CANCEL)) {
                                button.textProperty().bind(cancelText);
                                button.addEventFilter(ActionEvent.ACTION, event -> {
                                    WizardEvent wizardEvent = new WizardEvent(WizardEvent.CANCEL);
                                    fireEvent(wizardEvent);
                                    if (wizardEvent.isCancel()) {
                                        event.consume();
                                    }
                                });
                            } else if (type.equals(ButtonType.FINISH)) {
                                button.textProperty().bind(finishText);
                                button.addEventFilter(ActionEvent.ACTION, event -> {
                                    WizardEvent wizardEvent = new WizardEvent(WizardEvent.FINISH);
                                    fireEvent(wizardEvent);
                                    if (wizardEvent.isCancel()) {
                                        event.consume();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

        this.helpVisible.addListener((o, ov, nv) -> {
            if (nv) {
                if (!this.getButtonTypes().contains(buttonTypeHelp)) {
                    this.getButtonTypes().add(buttonTypeHelp);
                }
            } else {
                if (this.getButtonTypes().contains(buttonTypeHelp)) {
                    this.getButtonTypes().removeAll(buttonTypeHelp);
                }
            }
        });

        this.cancelVisible.addListener((o, ov, nv) -> {
            if (nv) {
                if (!this.getButtonTypes().contains(ButtonType.CANCEL)) {
                    this.getButtonTypes().add(ButtonType.CANCEL);
                }
            } else {
                if (this.getButtonTypes().contains(ButtonType.CANCEL)) {
                    this.getButtonTypes().removeAll(ButtonType.CANCEL);
                }
            }
        });
        //endregion

        this.selectedPageIndex.addListener((o, ov, nv) -> {
            int oldValue = ov.intValue();
            int newValue = nv.intValue();
            if (newValue < 0 || newValue > getPages().size() - 1) {
                throw new IndexOutOfBoundsException("索引超出范围。");
            } else if (oldValue != newValue) {

                //region 更新Next和Finish按钮
                if (newValue == this.pages.size() - 1) {
                    if (this.getButtonTypes().contains(ButtonType.NEXT)) {
                        this.getButtonTypes().remove(ButtonType.NEXT);
                    }
                    if (!this.getButtonTypes().contains(ButtonType.FINISH)) {
                        this.getButtonTypes().add(ButtonType.FINISH);
                    }
                } else if (newValue == this.pages.size() - 2) {
                    if (this.getButtonTypes().contains(ButtonType.FINISH)) {
                        this.getButtonTypes().remove(ButtonType.FINISH);
                    }
                    if (!this.getButtonTypes().contains(ButtonType.NEXT)) {
                        this.getButtonTypes().addAll(ButtonType.NEXT);
                    }
                }
                //endregion

                //region 设置页面，更新Header和Content
                this.selectedPage.set(this.pages.get(newValue));
                if (this.showHeader.get()) {
                    this.setHeader(this.getSelectedPage().getHeader());
                }
                this.setContent(this.getSelectedPage().getContent());
                //endregion

                //region 根据选中页面设置按钮可见性和可用性
                if (this.getSelectedPage().getHelpVisible()) {
                    if (!this.getButtonTypes().contains(buttonTypeHelp)) {
                        this.getButtonTypes().add(buttonTypeHelp);
                    }
                } else if (!this.getHelpVisible()) {
                    if (this.getButtonTypes().contains(buttonTypeHelp)) {
                        this.getButtonTypes().remove(buttonTypeHelp);
                    }
                }
                if (getButtonTypes().contains(ButtonType.NEXT)) {
                    ((Button) lookupButton(ButtonType.NEXT)).disableProperty().bind(this.getSelectedPage().allowNextProperty().not());
                }
                if (getButtonTypes().contains(ButtonType.PREVIOUS)) {
                    ((Button) lookupButton(ButtonType.PREVIOUS)).disableProperty().bind(this.getSelectedPage().allowPreviousProperty().not().or(selectedPageIndex.isEqualTo(0)));
                }
                if (getButtonTypes().contains(ButtonType.CANCEL)) {
                    ((Button) lookupButton(ButtonType.CANCEL)).disableProperty().bind(this.getSelectedPage().allowCancelProperty().not());
                }
                //endregion

                //region 触发SelectedPageChanged事件
                WizardPage oldPage = (oldValue < 0 || oldValue > getPages().size() - 1) ? null : getPages().get(oldValue);
                fireEvent(new WizardEvent(WizardEvent.SELECTEDPAGECHANGED, oldPage, getPages().get(newValue), newValue > oldValue ? Direction.Forward : Direction.Backward));
                //endregion
            }
        });

        if (ownerDialog != null) {
            ownerDialog.setOnShown(event -> {
                this.getButtonTypes().addAll(ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.CANCEL);//必须在前面
                this.setSelectedPageIndex(0);
            });
        }
    }

    //region 属性get&set - 按钮文本及可用性
    public String getHelpText()
    {
        return helpText.get();
    }

    public StringProperty helpTextProperty()
    {
        return helpText;
    }

    public void setHelpText(String helpText)
    {
        this.helpText.set(helpText);
    }

    public String getPreviousText()
    {
        return previousText.get();
    }

    public StringProperty previousTextProperty()
    {
        return previousText;
    }

    public void setPreviousText(String previousText)
    {
        this.previousText.set(previousText);
    }

    public String getNextText()
    {
        return nextText.get();
    }

    public StringProperty nextTextProperty()
    {
        return nextText;
    }

    public void setNextText(String nextText)
    {
        this.nextText.set(nextText);
    }

    public String getFinishText()
    {
        return finishText.get();
    }

    public StringProperty finishTextProperty()
    {
        return finishText;
    }

    public void setFinishText(String finishText)
    {
        this.finishText.set(finishText);
    }

    public String getCancelText()
    {
        return cancelText.get();
    }

    public StringProperty cancelTextProperty()
    {
        return cancelText;
    }

    public void setCancelText(String cancelText)
    {
        this.cancelText.set(cancelText);
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

    public boolean getCancelVisible()
    {
        return cancelVisible.get();
    }

    public BooleanProperty cancelVisibleProperty()
    {
        return cancelVisible;
    }

    public void setCancelVisible(boolean cancelVisible)
    {
        this.cancelVisible.set(cancelVisible);
    }
    //endregion

    //region 属性get&set - 其他
    public boolean getShowHeader()
    {
        return showHeader.get();
    }

    public BooleanProperty showHeaderProperty()
    {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader)
    {
        this.showHeader.set(showHeader);
    }

    public boolean getShowHeaderImage()
    {
        return showHeaderImage.get();
    }

    public BooleanProperty showHeaderImageProperty()
    {
        return showHeaderImage;
    }

    public void setShowHeaderImage(boolean showHeaderImage)
    {
        this.showHeaderImage.set(showHeaderImage);
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

    public int getSelectedPageIndex()
    {
        return selectedPageIndex.get();
    }

    public IntegerProperty selectedPageIndexProperty()
    {
        return selectedPageIndex;
    }

    public void setSelectedPageIndex(int selectedPageIndex)
    {
        if (selectedPageIndex < 0 || selectedPageIndex > getPages().size() - 1) {
            throw new IndexOutOfBoundsException("索引超出范围。");
        } else if (this.getSelectedPageIndex() != selectedPageIndex) {
            WizardPage oldPage = this.getSelectedPage();
            Direction dir = selectedPageIndex > this.getSelectedPageIndex() ? Direction.Forward : Direction.Backward;
            if (oldPage != null && dir.equals(Direction.Forward)) {
                WizardEvent event = new WizardEvent(WizardEvent.PAGEVALIDATING);
                oldPage.fireEvent(event);
                if (!event.isValid()) {
                    System.out.println(event.getErrorText());
                    return;
                }
            }
            WizardEvent event = new WizardEvent(WizardEvent.SELECTEDPAGECHANGING, oldPage, getPages().get(selectedPageIndex), dir);
            fireEvent(event);
            if (!event.isCancel()) {
                this.selectedPageIndex.set(selectedPageIndex);
            }
        }
    }

    public WizardPage getSelectedPage()
    {
        return selectedPage.get();
    }

    public ObjectProperty<WizardPage> selectedPageProperty()
    {
        return selectedPage;
    }

    public void setSelectedPage(WizardPage selectedPage)
    {
        int index = this.getPages().indexOf(selectedPage);
        if (index < 0) {
            throw new IndexOutOfBoundsException("指定页面不在向导中。");
        } else {
            this.setSelectedPageIndex(index);
        }
    }

    public ObservableList<WizardPage> getPages()
    {
        return pages;
    }
    //endregion

    //region 事件

    private final ObjectProperty<EventHandler<WizardEvent>> onFinish = new ObjectPropertyBase<EventHandler<WizardEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(WizardEvent.FINISH, get());
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
    private final ObjectProperty<EventHandler<WizardEvent>> onCancel = new ObjectPropertyBase<EventHandler<WizardEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(WizardEvent.CANCEL, get());
        }

        @Override
        public Object getBean()
        {
            return this;
        }

        @Override
        public String getName()
        {
            return "onCancel";
        }
    };
    private final ObjectProperty<EventHandler<WizardEvent>> onHelp = new ObjectPropertyBase<EventHandler<WizardEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(WizardEvent.HELP, get());
        }

        @Override
        public Object getBean()
        {
            return this;
        }

        @Override
        public String getName()
        {
            return "onHelp";
        }
    };
    private final ObjectProperty<EventHandler<WizardEvent>> onSelectedPageChanged = new ObjectPropertyBase<EventHandler<WizardEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(WizardEvent.SELECTEDPAGECHANGED, get());
        }

        @Override
        public Object getBean()
        {
            return this;
        }

        @Override
        public String getName()
        {
            return "onSelectedPageChanged";
        }
    };
    private final ObjectProperty<EventHandler<WizardEvent>> onSelectedPageChanging = new ObjectPropertyBase<EventHandler<WizardEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(WizardEvent.SELECTEDPAGECHANGING, get());
        }

        @Override
        public Object getBean()
        {
            return this;
        }

        @Override
        public String getName()
        {
            return "onSelectedPageChanging";
        }
    };

    public EventHandler<WizardEvent> getOnFinish()
    {
        return onFinish.get();
    }

    public ObjectProperty<EventHandler<WizardEvent>> onFinishProperty()
    {
        return onFinish;
    }

    public void setOnFinish(EventHandler<WizardEvent> onFinish)
    {
        this.onFinish.set(onFinish);
    }

    public EventHandler<WizardEvent> getOnCancel()
    {
        return onCancel.get();
    }

    public ObjectProperty<EventHandler<WizardEvent>> onCancelProperty()
    {
        return onCancel;
    }

    public void setOnCancel(EventHandler<WizardEvent> onCancel)
    {
        this.onCancel.set(onCancel);
    }

    public EventHandler<WizardEvent> getOnHelp()
    {
        return onHelp.get();
    }

    public ObjectProperty<EventHandler<WizardEvent>> onHelpProperty()
    {
        return onHelp;
    }

    public void setOnHelp(EventHandler<WizardEvent> onHelp)
    {
        this.onHelp.set(onHelp);
    }

    public EventHandler<WizardEvent> getOnSelectedPageChanged()
    {
        return onSelectedPageChanged.get();
    }

    public ObjectProperty<EventHandler<WizardEvent>> onSelectedPageChangedProperty()
    {
        return onSelectedPageChanged;
    }

    public void setOnSelectedPageChanged(EventHandler<WizardEvent> onSelectedPageChanged)
    {
        this.onSelectedPageChanged.set(onSelectedPageChanged);
    }

    public EventHandler<WizardEvent> getOnSelectedPageChanging()
    {
        return onSelectedPageChanging.get();
    }

    public ObjectProperty<EventHandler<WizardEvent>> onSelectedPageChangingProperty()
    {
        return onSelectedPageChanging;
    }

    public void setOnSelectedPageChanging(EventHandler<WizardEvent> onSelectedPageChanging)
    {
        this.onSelectedPageChanging.set(onSelectedPageChanging);
    }
    //endregion
}
