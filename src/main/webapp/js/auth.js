// Login Form Handler
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const loginText = document.getElementById('loginText');
        const loginLoader = document.getElementById('loginLoader');
        const errorMessage = document.getElementById('errorMessage');
        
        loginText.style.display = 'none';
        loginLoader.style.display = 'inline-block';
        errorMessage.classList.remove('show');
        
        const formData = new FormData(loginForm);
        
        try {
            const response = await fetch('login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(formData).toString()
            });
            
            const data = await response.json();
            
            if (data.success) {
                window.location.href = data.redirectUrl;
            } else {
                errorMessage.textContent = data.message;
                errorMessage.classList.add('show');
            }
        } catch (error) {
            errorMessage.textContent = 'An error occurred. Please try again.';
            errorMessage.classList.add('show');
        } finally {
            loginText.style.display = 'inline';
            loginLoader.style.display = 'none';
        }
    });
}

// Register Form Handler
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const registerText = document.getElementById('registerText');
        const registerLoader = document.getElementById('registerLoader');
        const errorMessage = document.getElementById('errorMessage');
        
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (password !== confirmPassword) {
            errorMessage.textContent = 'Passwords do not match';
            errorMessage.classList.add('show');
            return;
        }
        
        registerText.style.display = 'none';
        registerLoader.style.display = 'inline-block';
        errorMessage.classList.remove('show');
        
        const formData = new FormData(registerForm);
        
        try {
            const response = await fetch('register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(formData).toString()
            });
            
            const data = await response.json();
            
            if (data.success) {
                window.location.href = data.redirectUrl;
            } else {
                errorMessage.textContent = data.message;
                errorMessage.classList.add('show');
            }
        } catch (error) {
            errorMessage.textContent = 'An error occurred. Please try again.';
            errorMessage.classList.add('show');
        } finally {
            registerText.style.display = 'inline';
            registerLoader.style.display = 'none';
        }
    });
}
