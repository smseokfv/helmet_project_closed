package com.example.windowUI.loginUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.example.MainWindow;

/**
 * 모달(Modal) 방식으로 작동하며, 앱에서 전달받은 사용자 인증 정보로 인증하는 로그인 다이얼로그입니다.
 */
public class LoginDialog extends JDialog implements ActionListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    // ---------------------------------------------
    // 1. 컴포넌트 및 상태 변수
    // ---------------------------------------------
    private final MainWindow mainWindow;
    private final JTextField idField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private boolean isAuthenticated = false; // 로그인 성공 여부 플래그

    /**
     * LoginDialog를 생성합니다.
     * @param parent 부모 JFrame (MainWindow)
     */
    public LoginDialog(MainWindow parent) {
        // 부모 창이 닫히기 전까지는 이 창에만 상호작용 가능하도록 모달(true)로 설정
        super(parent, "로그인", true);
        this.mainWindow = parent; 
        
        // 닫기 버튼 클릭 시 바로 닫히도록 설정
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // 창 닫기 이벤트 처리
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // X 버튼 클릭 시 바로 닫기 (isAuthenticated는 false로 유지)
                dispose();
            }
        });
        
        // UI 설정
        setupLayout();

        idField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("로그인");
        loginButton.addActionListener(this); // 이벤트 리스너 등록

        // UI 컴포넌트 추가
        addComponents();

        // 창 크기 조정 및 위치 설정
        pack(); 
        setLocationRelativeTo(parent); // 부모 창 중앙에 위치
    }

    private void setupLayout() {
        // 패널 생성 및 레이아웃 설정
        JPanel panel = new JPanel(new GridBagLayout());
        setContentPane(panel);
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // 간격 설정
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 아이디 레이블 및 필드
        gbc.gridx = 0; gbc.gridy = 0;
        getContentPane().add(new JLabel("아이디:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        getContentPane().add(idField, gbc);

        // 비밀번호 레이블 및 필드
        gbc.gridx = 0; gbc.gridy = 1;
        getContentPane().add(new JLabel("비밀번호:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        getContentPane().add(passwordField, gbc);

        // 로그인 버튼
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST; 
        getContentPane().add(loginButton, gbc);
    }

    // ---------------------------------------------
    // 3. 인증 로직
    // ---------------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String inputId = idField.getText();
            // JPasswordField에서 비밀번호를 가져올 때는 보안상의 이유로 char[] 배열을 String으로 변환해야 함
            String inputPassword = new String(passwordField.getPassword()); 

            System.out.println("[LoginDialog] 로그인 시도 - 입력한 아이디: \"" + inputId + "\", 비밀번호: \"" + inputPassword + "\"");
            
            // MainWindow의 userLoginList에서 확인
            boolean result = mainWindow.checkUserLogin(inputId, inputPassword);
            System.out.println("[LoginDialog] 인증 결과: " + result);
            
            if (result) {
                // 인증 성공
                isAuthenticated = true;
                System.out.println("[LoginDialog] 인증 성공");
                dispose(); // 창 닫기
            } else {
                // 인증 실패
                System.out.println("[LoginDialog] 인증 실패");
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 틀렸습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // 비밀번호 필드 초기화
            }
        }
    }
    public boolean isAuthenticated() {
        return isAuthenticated;
    }    
};

