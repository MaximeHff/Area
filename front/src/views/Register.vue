<template>
    <div id="register">
        <b-image src="https://images.unsplash.com/photo-1667925459217-e7b7a9797409?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80"/>
        <section class="registerForm">
            <h3>Create your account</h3>
            <div class="names">
                <b-field label="First Name" label-position="on-border" :type="register.firstName.error != '' ? 'is-danger' : ''" :message="register.firstName.error">
                    <b-input v-model="register.firstName.value" type="firstname" @input="checkNameInput($event, 'firstName')"></b-input>
                </b-field>
                <b-field label="Last Name" label-position="on-border" :type="register.lastName.error != '' ? 'is-danger' : ''" :message="register.lastName.error">
                    <b-input v-model="register.lastName.value" type="lastname" @input="checkNameInput($event, 'lastName')"></b-input>
                </b-field>
            </div>
            <b-field label="E-mail" label-position="on-border" :type="register.email.error != '' ? 'is-danger' : ''" :message="register.email.error">
                <b-input type="email" v-model="register.email.value" @input="checkEmail($event)"></b-input>
            </b-field>
            <b-field label="Password" label-position="on-border" :type="register.password.error != '' ? 'is-danger' : ''" :message="register.password.error">
                <b-input v-model="register.password.value" type="password" password-reveal @input="checkPassword($event, 'password')" oncopy="return false" onpaste="return false"></b-input>
            </b-field>
            <b-field label="Confirm password" label-position="on-border" :type="register.confirmPassword.error != '' ? 'is-danger' : ''" :message="register.confirmPassword.error">
                <b-input v-model="register.confirmPassword.value" type="password" password-reveal @input="checkPassword($event, 'confirmPassword')" oncopy="return false" onpaste="return false"></b-input>
            </b-field>
            <div class="buttonContent">
                <div ref="registerButton" class="registerButton" @mouseover="validate == false ? moveButton() : ''">
                    <b-button @click="validate == true ? sendRegister() : ''" :type="validate == true ? 'is-success is-light' : 'is-danger is-light'">Register</b-button>
                </div>
            </div>
            <div class="login">
                <router-link to="/login">
                    <a>Already an account ? Login</a>
                </router-link>
            </div>
        </section>
    </div>
</template>

<script lang="ts">
import vue from 'vue';
import { Register } from '../types/index'

export default vue.extend({
    data() {
        return {
            register: { /** It's a variable that contains all the data of the register form. */
                firstName: {
                    value: "",
                    error: "",
                    valide: false,
                },
                lastName: {
                    value: "",
                    error: "",
                    valide: false,
                },
                email: {
                    value: "",
                    error: "",
                    valide: false,
                },
                password: {
                    value: "",
                    error: "",
                    valide: false,
                },
                confirmPassword: {
                    value: "",
                    error: "",
                    valide: false,
                }
            } as Register,
            validate: false, /** If this variable is true all the fields form are valides. */
            timeout: false, /** This variable is used to block the time of the button animation */
        }
    },
    methods: {
        /**
         * It's a function that check if the entire form is valide or not.
         * @data {Object} register
         * @data {Boolean} validate
         */
        valideForm(): void {
            if (this.register.firstName.valide == true &&
            this.register.lastName.valide == true &&
            this.register.email.valide == true &&
            this.register.password.valide == true &&
            this.register.confirmPassword.valide == true) {
                this.$refs.registerButton.style.left = "50px";
                this.validate = true;
            } else
                this.validate = false;
        },
        /**
         * It's a function that move the button to the left or to the right when the form is not valid.
         * @data {Boolean} timeout
         */
        moveButton(): void {
            let button = this.$refs.registerButton as HTMLElement;
            button.style.transitionProperty = "left";
            button.style.transitionDuration = "0.3s";
            if ((button.style.left == "100px" || button.style.left == "") && this.timeout == false) {
                button.style.left = "0px";
            } else if (this.timeout == false) {
                button.style.left = "100px"
            }
            this.timeout = true;
            window.setTimeout(() => {
                this.timeout = false;
            }, 290)
        },
        /**
         * It's a function that check if the name is valide or not.
         * @param {string} input - Text input
         * @param {string} type - firstName or lastName
         * @data {Object} register
         */
        checkNameInput(input: string, type: string): void {
            const name_regex = (/^[a-zA-Z]+$/)
            if (input == "") {
                this.register[type].error = "Cannot be empty";
                this.register[type].valide = false;
            } else if (!input.match(name_regex)) {
                this.register[type].error = "Only letters are accepted";
                this.register[type].valide = false;
            } else if (input.length >= 25) {
                this.register[type].error = "Maximum 25 characters are allowed";
                this.register[type].valide = false;
            } else {
                this.register[type].error = "";
                this.register[type].valide = true;
            }
            this.valideForm();
        },
        /**
         * It's a function that check if the email is valide or not.
         * @param {string} input - Text input
         * @data {Object} login
         */
        checkEmail(input: string): void {
            const email_regex = (/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
            if (input == "") {
                this.register.email.error = "Cannot be empty";
                this.register.email.valide = false;
            } else if (!input.match(email_regex)) {
                this.register.email.error = "One or some elements are missing in your email";
                this.register.email.valide = false;
            } else {
                this.register.email.error = "";
                this.register.email.valide = true;
            }
            this.valideForm();
        },
        /**
         * It's a function that check if the password is valide or not.
         * @param {string} input - Text input
         * @param {string} type - Password or confirmPassword
         * @data {Object} register
         */
        checkPassword(input: string, type: string): void {
            const password_regex = (/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*"'/~_.+:;=-]).{8,}$/)
            if (type == "password") {
                if (!input.match(password_regex)) {
                    this.register[type].error = "Minimum requirement for password is : 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character"
                    this.register[type].valide = false;
                } else {
                    this.register[type].error = ""
                    this.register[type].valide = true;
                } if (this.register.password.value != this.register.confirmPassword.value) {
                    this.register.confirmPassword.error = "Password are not the same"
                    this.register.confirmPassword.valide = false;
                } else {
                    this.register.confirmPassword.error = ""
                    this.register.confirmPassword.valide = true;
                }
            } else {
                if (this.register.password.value != this.register.confirmPassword.value) {
                    this.register[type].error = "Password are not the same"
                    this.register[type].valide = false;
                } else {
                    this.register[type].error = ""
                    this.register[type].valide = true;
                }
            }
            this.valideForm();
        },
        /**
         * It's a function that send the register form to the server.
         * @data {Object} register
         * @async
         */
        async sendRegister(): Promise<any> {
            try {
                let {data: resp} = await this.$axios.post('/users', {
                    'firstName': this.register.firstName.value,
                    'lastName': this.register.lastName.value,
                    'email': this.register.email.value,
                    'password': this.register.password.value,
                }, {
                    headers: {
                        Authorization: this.$store.getters.userToken || "noToken",
                    }
                })
                localStorage.setItem('usr-token', resp.token);
                this.$store.commit('updateToken', resp.token);
                this.$router.push('/home');
            } catch (err) {
                if (err.response)
                    this.notification(err.response.data.message, 'is-danger');
            }
        }
    }
})
</script>

<style scoped lang="scss">
#register {
    display: flex;
    height: 100vh;
    width: 100vw;
    :deep(figure) {
        height: 100%;
        width: 100%;
        img {
            width: 100%;
            height: 100%;
            overflow: hidden;
            background-size: 100% 100%;
        }
    }
    .registerForm {
        background-color: white;
        min-width: 400px;
        width: 450px;
        height: fit-content;
        position: absolute;
        box-shadow: 0 0 30px 1px black;
        border-radius: 20px;
        padding: 20px 25px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        h3 {
            font-size: 40px;
            margin-bottom: 25px;
            font-family: Bebas Regular;
        }
        .names {
            display: flex;
            justify-content: space-between;
            :deep(.field) {
                width: 48%;
            }
        }
        :deep(.field) {
            margin-bottom: 20px;
        }
        .login {
            margin-top: 15px;
        }
    }
    .buttonContent {
        transform: translate(calc(50% - 100px), 0%);
        position: relative;
        .registerButton {
            transition-property: left;
            transition-duration: 0.5s;
            width: fit-content;
            left: 50px;
            position: relative;
        }
    }
}
</style>
