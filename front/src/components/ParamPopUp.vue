<template>
    <div id="ParamPopUp">
        <div class="background"/>
        <div class="popUp" :class="{ 'darkMode' : $store.getters.darkMode }">
            <p>Please put your {{getParamName().toLowerCase()}}</p>
            <div class="params">
                <b-field :type="errorMessage != '' ? 'is-danger' : ''" :message="errorMessage != '' ? errorMessage : ''">
                    <b-input :placeholder="getParamName()" v-model="area[type + 'Param']" @input="$emit('save'), checkInput()"></b-input>
                </b-field>
                <b-dropdown aria-role="list" v-if="type == 'reaction' && getInjectParams().length != 0">
                    <template #trigger="{ active }">
                        <b-button
                            label="Injected params"
                            type="is-primary"
                            :icon-right="active ? 'chevron-up' : 'chevron-down'" />
                    </template>
                    <b-dropdown-item v-for="param of getInjectParams()" :key="param" @click="appendInput(param), checkInput()" aria-role="listitem"> {{ param }} </b-dropdown-item>
                </b-dropdown>
            </div>
            <div class="buttons">
                <b-button type="is-danger is-light" @click="$emit('close')">Cancel</b-button>
                <b-button type="is-success is-light" :disabled="errorMessage === '' ? false : true" @click="$emit('next')">Done</b-button>
            </div>
        </div>
    </div>
</template>

<script scoped lang="ts">
import vue from 'vue';

export default vue.extend({
    data() {
        return {
            errorMessage: '', /** Message error of the input */
        };
    },
    mounted() {
        if (this.getParamName() === '') {
            this.$emit('next')
            this.$emit('save')
        }
        this.checkInput();
    },
    props: {
        services: Array,
        area: Object,
        type: String,
    },
    methods: {
        /**
         * It's a function that checks if the input is empty or not and set the error message.
         * @data {string} type
         * @data {Object} area
         * @data {String} errorMessage
         */
        checkInput(): void {
            if (this.area[this.type + 'Param'] === '') {
                this.errorMessage = "Input cannot be empty !";
                return;
            } else {
                this.errorMessage = '';
            }
        },
        /**
         * It's a function that appends the value of the injected parameter to the input.
         * @data {Object} area
         * @data {string} type
         */
        appendInput(value: string) {
            this.area[this.type + 'Param'] = this.area[this.type + 'Param'] + '%' + value + '%';
        },
        /**
         * It's a function that returns the name
         * @data {Object} area
         * @data {Array} services
         */
        getInjectParams() {
            let service = this.services.find(service => service.id === this.area['actionServiceId']);
            let area: Object;
            if (service != undefined)
                area = service['actions'].find(actrea => actrea.id === this.area['actionId']).availableInjectParams;
            return area;
        },
        /**
         * It's a function that returns the name of the parameter of the action or reaction.
         * @data {Object} area
         * @data {Array} services
         * @data {String} type
         * @return {string} - Return the actual selected service name.
         */
        getParamName(): string {
            if (this.area[this.type + "Id"] == -1 || this.services[0] === null) return;
            let service = this.services.find(
                (service) => service.id == this.area[this.type + "ServiceId"]
            );
            let paramName = service[this.type + "s"].find(
                (actrea) => actrea.id === this.area[this.type + "Id"]
            )[this.type + "ParamName"];
            return paramName;
        },
    }
})
</script>

<style lang="scss" scoped>
#ParamPopUp {
    top: 0px;
    left: 0px;
    position: absolute;
    height: 100%;
    width: 100%;
    z-index: 10;
    p {
        font-family: Hitmo Regular;
    }
    .background {
        opacity: 50%;
        width: 100%;
        height: 100%;
        background-color: black;
        position: absolute;
    }
    .popUp {
        transform: translate(-50%, -50%);
        top: 50%;
        left: 50%;
        position: absolute;
        background-color: white;
        border-radius: 20px;
        padding: 18px;
        width: 560px;
        height: auto;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        &.darkMode {
            background-color: #21242b;
            >p {
                color: white;
            }
        }
        >p {
            margin-bottom: 5px;
        }
        .params {
            display: flex;
            flex-direction: column;
            align-items: center;
            >:deep(div) {
                width: 85%;
                .help {
                    margin-block: 0px;
                }
                .dropdown-menu {
                    width: 100%;
                    margin-top: 10px;
                    top: 35px !important;
                }
                .dropdown-trigger {
                    width: 100%;
                    button {
                        width: 100%;
                        span {
                            margin-left: 10px;
                            svg {
                                height: 20px;
                            }
                        }
                    }
                }
            }
        }
        .buttons {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
        }
    }
}
</style>
