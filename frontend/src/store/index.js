import { createStore } from 'vuex'
import api from '../api'

export default createStore({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    courses: [],
    currentCourse: null,
    userProgress: null
  },

  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    SET_COURSES(state, courses) {
      state.courses = courses
    },
    SET_CURRENT_COURSE(state, course) {
      state.currentCourse = course
    },
    SET_USER_PROGRESS(state, progress) {
      state.userProgress = progress
    },
    LOGOUT(state) {
      state.user = null
      state.token = null
      state.courses = []
      state.questionBanks = []
      state.currentCourse = null
      state.userProgress = null
      localStorage.removeItem('token')
    }
  },

  actions: {
    // 初始化用户信息
    async initializeAuth({ commit, state }) {
      const token = localStorage.getItem('token')
      if (token && !state.user) {
        try {
          // 设置token到state
          commit('SET_TOKEN', token)
          // 获取用户信息
          const response = await api.get('/auth/me')
          commit('SET_USER', response.data.data)
        } catch (error) {
          // 如果token无效，清除token
          commit('LOGOUT')
        }
      }
    },

    async login({ commit }, credentials) {
      try {
        const response = await api.post('/auth/login', credentials)
        const { token, user } = response.data.data
        commit('SET_TOKEN', token)
        commit('SET_USER', user)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async register({ commit }, userData) {
      try {
        const formData = new FormData()
        formData.append('userData', JSON.stringify(userData))
        formData.append('facePhoto', userData.facePhoto)

        const response = await api.post('/auth/register', formData)
        return response.data
      } catch (error) {
        throw error
      }
    },

    logout({ commit }) {
      commit('LOGOUT')
    },

    async fetchCourses({ commit }) {
      try {
        const response = await api.get('/courses')
        commit('SET_COURSES', response.data.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async fetchCourseById({ commit }, courseId) {
      try {
        const response = await api.get(`/courses/${courseId}`)
        commit('SET_CURRENT_COURSE', response.data.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async fetchUserCourseProgress({ commit }, { userId, courseId }) {
      try {
        const response = await api.get(`/courses/${courseId}/progress/${userId}`)
        commit('SET_USER_PROGRESS', response.data.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async updateUserCourseProgress({ commit }, { userId, courseId, currentTime, completedChapters }) {
      try {
        const response = await api.put(`/courses/${courseId}/progress/${userId}`, {
          currentTime,
          completedChapters
        })
        commit('SET_USER_PROGRESS', response.data.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async completeCourse({ commit }, { userId, courseId }) {
      try {
        const response = await api.post(`/courses/${courseId}/complete/${userId}`)
        commit('SET_USER_PROGRESS', response.data.data)
        return response.data
      } catch (error) {
        throw error
      }
    },

    async resetCourseProgress({ commit }, { userId, courseId }) {
      try {
        const response = await api.delete(`/courses/${courseId}/progress/${userId}`)
        commit('SET_USER_PROGRESS', null)
        return response.data
      } catch (error) {
        throw error
      }
    },
  },

  getters: {
    isAuthenticated: state => !!state.token && !!state.user,
    userRole: state => state.user ? state.user.role : null,
    userId: state => state.user ? state.user.id : null,
    currentUser: state => state.user,
    availableCourses: state => state.courses,
    availableQuestionBanks: state => state.questionBanks,
    currentCourse: state => state.currentCourse,
    userProgress: state => state.userProgress
  }
})
