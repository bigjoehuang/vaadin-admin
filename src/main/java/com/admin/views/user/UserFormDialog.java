package com.admin.views.user;

import com.admin.component.BaseFormDialog;
import com.admin.entity.User;
import com.admin.service.FileService;
import com.admin.service.UserService;
import com.admin.util.I18NUtil;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.Unit;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * 用户表单对话框
 * 用于新增和编辑用户
 *
 * @author Admin
 * @date 2024-01-01
 */
public class UserFormDialog extends BaseFormDialog<User> {

    private final UserService userService;
    private final FileService fileService;
    private final Runnable refreshCallback;

    private TextField userNameField;
    private PasswordField passwordField;
    private TextField nicknameField;
    private EmailField emailField;
    private TextField phoneField;
    private Checkbox enabledCheckbox;
    private Image avatarImage;
    private Upload avatarUpload;
    private MemoryBuffer avatarBuffer;
    private byte[] avatarBytes;
    private boolean avatarChanged;

    /**
     * 构造函数
     *
     * @param userService     用户服务
     * @param fileService     文件服务
     * @param isEdit          是否为编辑模式
     * @param refreshCallback 刷新回调
     */
    public UserFormDialog(UserService userService, FileService fileService, boolean isEdit, Runnable refreshCallback) {
        super(User.class, isEdit);
        this.userService = userService;
        this.fileService = fileService;
        this.refreshCallback = refreshCallback;
        // 设置对话框标题
        if (isEdit) {
            setHeaderTitle(I18NUtil.get("user.edit"));
        } else {
            setHeaderTitle(I18NUtil.get("user.new"));
        }
    }

    @Override
    protected void buildFormFields() {
        userNameField = new TextField(I18NUtil.get("user.userName"));
        userNameField.setRequired(true);
        userNameField.setRequiredIndicatorVisible(true);
        userNameField.setWidthFull();
        userNameField.setPlaceholder(I18NUtil.get("user.placeholder.userName"));
        // 通过Java代码设置label颜色，确保在获得焦点时可见
        userNameField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        userNameField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");
        if (isEdit) {
            userNameField.setReadOnly(true);
            userNameField.setHelperText(I18NUtil.get("user.helper.userName.edit"));
        }

        passwordField = new PasswordField(I18NUtil.get("user.password"));
        passwordField.setWidthFull();
        passwordField.setPlaceholder(isEdit ? I18NUtil.get("user.placeholder.password.edit") : I18NUtil.get("user.placeholder.password"));
        // 通过Java代码设置label颜色
        passwordField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        passwordField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");
        if (!isEdit) {
            passwordField.setRequired(true);
            passwordField.setRequiredIndicatorVisible(true);
        }

        nicknameField = new TextField(I18NUtil.get("user.nickname"));
        nicknameField.setWidthFull();
        nicknameField.setPlaceholder(I18NUtil.get("user.placeholder.nickname"));
        // 通过Java代码设置label颜色
        nicknameField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        nicknameField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        emailField = new EmailField(I18NUtil.get("user.email"));
        emailField.setWidthFull();
        emailField.setPlaceholder(I18NUtil.get("user.placeholder.email"));
        // 通过Java代码设置label颜色
        emailField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        emailField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        phoneField = new TextField(I18NUtil.get("user.phone"));
        phoneField.setWidthFull();
        phoneField.setPlaceholder(I18NUtil.get("user.placeholder.phone"));
        // 通过Java代码设置label颜色
        phoneField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        phoneField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        enabledCheckbox = new Checkbox(I18NUtil.get("user.status"));
        enabledCheckbox.setValue(true);

        // 头像上传组件
        avatarImage = new Image();
        avatarImage.setWidth(100, Unit.PIXELS);
        avatarImage.setHeight(100, Unit.PIXELS);
        avatarImage.setAlt(I18NUtil.get("user.avatar"));
        avatarImage.getElement().getStyle().setBorderRadius("50%");
        avatarImage.getElement().getStyle().setBorder("2px solid var(--lumo-primary-color)");

        avatarBuffer = new MemoryBuffer();
        avatarUpload = new Upload(avatarBuffer);
        avatarUpload.setMaxFiles(1);
        avatarUpload.setAcceptedFileTypes("image/*");
        avatarUpload.setWidthFull();

        // 上传成功事件处理
        avatarUpload.addSucceededListener(event -> {
            try {
                // 读取上传的文件内容
                InputStream inputStream = avatarBuffer.getInputStream();
                avatarBytes = inputStream.readAllBytes();
                avatarChanged = true;
                
                // 更新头像预览
                StreamResource resource = new StreamResource("avatar", () -> new ByteArrayInputStream(avatarBytes));
                avatarImage.setSrc(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 上传失败事件处理
        avatarUpload.addFailedListener(event -> {
            showError(I18NUtil.get("user.validation.avatar.upload.failed"));
        });

        // 水平布局，包含头像预览和上传组件
        HorizontalLayout avatarLayout = new HorizontalLayout();
        avatarLayout.setAlignItems(Alignment.CENTER);
        avatarLayout.setSpacing(true);
        avatarLayout.setWidthFull();
        
        // 头像预览区域
        HorizontalLayout previewLayout = new HorizontalLayout();
        previewLayout.setAlignItems(Alignment.CENTER);
        previewLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        previewLayout.setWidth("150px");
        previewLayout.add(avatarImage);
        
        // 上传组件区域
        HorizontalLayout uploadLayout = new HorizontalLayout();
        uploadLayout.setAlignItems(Alignment.CENTER);
        uploadLayout.setFlexGrow(1, uploadLayout);
        uploadLayout.add(avatarUpload);
        
        avatarLayout.add(previewLayout, uploadLayout);

        formLayout.add(userNameField, 2);
        formLayout.add(passwordField, 2);
        formLayout.add(nicknameField, 2);
        formLayout.add(emailField, 2);
        formLayout.add(phoneField, 2);
        formLayout.add(avatarLayout, 2);
        formLayout.add(enabledCheckbox, 2);
    }

    @Override
    protected void configureBinder() {
        // 手动绑定字段
        binder.forField(userNameField)
                .asRequired(I18NUtil.get("user.validation.userName.required"))
                .withValidator(new StringLengthValidator(I18NUtil.get("user.validation.userName.length"), 1, 50))
                .withValidator(userName -> {
                    if (isEdit) {
                        // 编辑模式下，用户名唯一性由Service层验证（已排除当前记录）
                        return true;
                    }
                    // 新增模式下检查用户名是否已存在
                    return !isUserNameExists(userName);
                }, I18NUtil.get("user.validation.userName.exists"))
                .bind(User::getUserName, User::setUserName);

        binder.forField(passwordField)
                .withValidator(password -> {
                    if (!isEdit) {
                        // 新增模式下，密码必填
                        return password != null && !password.trim().isEmpty();
                    } else {
                        // 编辑模式下，密码可以为空（保持原密码）
                        return true;
                    }
                }, I18NUtil.get("user.validation.password.required"))
                .withValidator(password -> {
                    if (password != null && !password.trim().isEmpty()) {
                        return password.length() >= 6;
                    }
                    return true; // 编辑模式下密码为空时跳过长度验证
                }, I18NUtil.get("user.validation.password.length"))
                .bind(User::getPassword, User::setPassword);

        binder.forField(nicknameField)
                .withValidator(nickname -> nickname == null || nickname.length() <= 50,
                        I18NUtil.get("user.validation.nickname.length"))
                .bind(User::getNickname, User::setNickname);

        binder.forField(emailField)
                .withValidator(new EmailValidator(I18NUtil.get("user.validation.email.invalid")))
                .bind(User::getEmail, User::setEmail);

        binder.forField(phoneField)
                .withValidator(phone -> phone == null || phone.matches("^1[3-9]\\d{9}$|^$"),
                        I18NUtil.get("user.validation.phone.invalid"))
                .bind(User::getPhone, User::setPhone);

        binder.forField(enabledCheckbox)
                .bind(User::getIsEnabled, User::setIsEnabled);
    }

    @Override
    protected void loadEntityData() {
        // 编辑模式下，数据通过 setEntity 方法设置
        if (isEdit && entity != null) {
            // 如果用户已有头像，加载头像
            String avatar = entity.getAvatar();
            if (avatar != null && !avatar.isEmpty()) {
                // 这里假设头像路径是可直接访问的URL
                avatarImage.setSrc(avatar);
            } else {
                // 设置默认头像
                avatarImage.setSrc("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 24 24' fill='none' stroke='%23999' stroke-width='1' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='7' r='4'/%3E%3Cpath d='M5 21h14a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z'/%3E%3C/svg%3E");
            }
        } else {
            // 新增模式，设置默认头像
            avatarImage.setSrc("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 24 24' fill='none' stroke='%23999' stroke-width='1' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='7' r='4'/%3E%3Cpath d='M5 21h14a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z'/%3E%3C/svg%3E");
        }
    }

    @Override
    protected void copyEntityFields(User source, User target) {
        target.setUserName(source.getUserName());
        // 密码字段不复制，由用户输入决定
        target.setNickname(source.getNickname());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAvatar(source.getAvatar());
        target.setIsEnabled(source.getIsEnabled());
        target.setDeleted(source.getDeleted());
    }

    @Override
    protected void save() {
        if (!validateAndWrite()) {
            return;
        }

        try {
            // 处理头像上传
            if (avatarChanged && avatarBytes != null) {
                // 创建MultipartFile对象
                MultipartFile multipartFile = new org.springframework.web.multipart.commons.CommonsMultipartFile(
                        new org.apache.commons.fileupload.disk.DiskFileItem(
                                "avatar",
                                "image/*",
                                false,
                                "avatar.jpg",
                                avatarBytes.length,
                                null) {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                return new ByteArrayInputStream(avatarBytes);
                            }
                        });
                
                // 上传头像，获取文件路径
                String avatarPath = fileService.uploadFile(multipartFile);
                // 设置头像路径
                entity.setAvatar(avatarPath);
            }

            // 编辑模式下，如果密码为空，则设置为null，让Service层保持原密码
            if (isEdit && (entity.getPassword() == null || entity.getPassword().trim().isEmpty())) {
                entity.setPassword(null);
            }

            if (isEdit) {
                userService.updateUser(entity);
                showSuccessAndClose(I18NUtil.get("user.update.success"));
            } else {
                userService.saveUser(entity);
                showSuccessAndClose(I18NUtil.get("user.save.success"));
            }
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (com.admin.exception.BusinessException e) {
            // 业务异常，显示友好的错误信息
            showError(e.getMessage());
        } catch (Exception e) {
            // 其他异常，显示通用错误信息
            showError(I18NUtil.get("error.operation.failed") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检查用户名是否已存在
     *
     * @param userName 用户名
     * @return 是否存在
     */
    private boolean isUserNameExists(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }
        User existUser = userService.getUserByUserName(userName);
        return existUser != null;
    }
}

