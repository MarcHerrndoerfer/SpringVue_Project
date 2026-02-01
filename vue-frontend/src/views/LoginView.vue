<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-5">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="card-title text-center mb-4">Login</h2>
            
            <div v-if="error" class="alert alert-danger" role="alert">
              {{ error }}
            </div>

            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="form.email"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password" 
                  v-model="form.password"
                  required
                />
              </div>

              <button 
                type="submit" 
                class="btn btn-primary w-100"
                :disabled="loading"
              >
                {{ loading ? 'Signing in...' : 'Sign in' }}
              </button>
            </form>

            <div class="text-center mt-3">
              <p class="mb-0">No account yet? <router-link to="/register">Register</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../services/auth'

const router = useRouter()
const form = ref({
  email: '',
  password: ''
})
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  try {
    const user = await login(form.value)
    localStorage.setItem('authUser', JSON.stringify(user))
    router.push('/')
  } catch (err: any) {
    error.value = err.message || 'An error occurred'
  } finally {
    loading.value = false
  }
}
</script>
