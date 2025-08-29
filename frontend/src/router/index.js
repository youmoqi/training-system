import {createRouter, createWebHistory} from 'vue-router'
import store from '../store'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/student/Login.vue'),
        meta: {requiresAuth: false}
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/student/Register.vue'),
        meta: {requiresAuth: false}
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/student/Dashboard.vue'),
        meta: {requiresAuth: true},
        children: [
            {
                path: '',
                redirect: '/dashboard/courses'
            },
            {
                path: 'courses',
                name: 'MyCourses',
                component: () => import('../views/student/Course/Courses.vue')
            },
            {
                path: 'course/:id',
                name: 'CourseDetail',
                component: () => import('../views/student/Course/CourseDetail.vue')
            },
            {
                path: 'question-banks',
                name: 'QuestionBanks',
                component: () => import('../views/student/QuestionBank/QuestionBanks.vue')
            },
            {
                path: 'question-bank/:questionBankId',
                name: 'QuestionBankPractice',
                component: () => import('../views/student/QuestionBank/QuestionBankPractice.vue')
            },
            {
                path: 'exam-papers',
                name: 'ExamPapers',
                component: () => import('../views/student/Exam/ExamPapers.vue')
            },
            {
                path: 'exam-papers/:id',
                name: 'ExamPaperDetail',
                component: () => import('../views/student/Exam/ExamPaperDetail.vue')
            },
            {
                path: 'exam-papers/:id/exam',
                name: 'ExamPaperExam',
                component: () => import('../views/student/Exam/ExamPaperExam.vue')
            },
            {
                path: 'exam-papers/:id/results/:resultId',
                name: 'ExamPaperResult',
                component: () => import('../views/student/Exam/ExamPaperResult.vue')
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/student/Profile.vue')
            },
            {
                path: 'join',
                name: 'JoinInvitation',
                component: () => import('../views/student/JoinInvitation.vue')
            },
            {
                path: 'exam-paper-history',
                name: 'ExamPaperHistory',
                component: () => import('../views/student/Exam/ExamPaperHistory.vue')
            },
            {
                path: 'exam-papers/:examPaperId/results/:resultId',
                name: 'ExamPaperResult',
                component: () => import('../views/student/Exam/ExamPaperResult.vue')
            },
            {
                path: 'question-bank-history',
                name: 'QuestionBankHistory',
                component: () => import('../views/student/QuestionBank/QuestionBankHistory.vue')
            },
            {
                path: 'question-bank-results/:resultId',
                name: 'QuestionBankResult',
                component: () => import('../views/student/QuestionBank/QuestionBankResult.vue')
            }
        ]
    },
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('../views/admin/Admin.vue'),
        meta: {requiresAuth: true, requiresAdmin: true},
        children: [
            {
                path: '',
                redirect: '/admin/courses'
            },
            {
                path: 'courses',
                name: 'AdminCourses',
                component: () => import('../views/admin/CourseManagement.vue')
            },
            {
                path: 'question-banks',
                name: 'AdminQuestionBanks',
                component: () => import('../views/admin/QuestionBank/QuestionBankManagement.vue')
            },
            {
                path: 'question-banks/:questionBankId/questions',
                name: 'AdminQuestionManagement',
                component: () => import('../views/admin/QuestionBank/QuestionManagement.vue')
            },
            {
                path: 'exam-papers',
                name: 'AdminExamPapers',
                component: () => import('../views/admin/Exam/ExamPaperManagement.vue')
            },
            {
                path: 'exam-papers/:id/auto-generate',
                name: 'AdminExamPaperAutoGenerate',
                component: () => import('../views/admin/Exam/ExamPaperAutoGenerate.vue')
            },
            {
                path: 'exam-papers/:id/questions',
                name: 'AdminExamPaperQuestions',
                component: () => import('../views/admin/Exam/ExamPaperQuestionsManagement.vue')
            },
            {
                path: 'exam-papers/:id',
                name: 'AdminExamPaperDetail',
                component: () => import('../views/admin/Exam/ExamPaperDetail.vue')
            },
            {
                path: 'statistics',
                name: 'AdminStatistics',
                component: () => import('../views/admin/Statistics.vue')
            },
            {
                path: 'users',
                name: 'AdminUsers',
                component: () => import('../views/admin/UserManagement.vue')
            },
            {
                path: 'registration-control',
                name: 'RegistrationControl',
                component: () => import('../views/admin/RegistrationControl.vue')
            },
            {
                path: 'invitations',
                name: 'AdminInvitations',
                component: () => import('../views/admin/InvitationManagement.vue')
            },
            {
                path: 'certificates',
                name: 'AdminCertificates',
                component: () => import('../views/admin/TrainingCertificateManagement.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
    // 确保认证状态已初始化
    if (!store.state.user && store.state.token) {
        try {
            await store.dispatch('initializeAuth')
        } catch (error) {
            store.dispatch('logout')
            next('/login')
            return
        }
    }

    const isAuthenticated = store.getters.isAuthenticated
    const userRole = store.getters.userRole?.code

    // 如果路由需要认证但用户未认证
    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login')
        return
    }

    // 如果路由需要管理员权限但用户不是管理员
    if (to.meta.requiresAdmin && !['SUPER_ADMIN'].includes(userRole)) {
        next('/dashboard')
        return
    }

    // 如果已认证用户访问登录页面，重定向到仪表板
    if (to.path === '/login' && isAuthenticated) {
        if (['SUPER_ADMIN'].includes(userRole)) {
            next('/admin')
        } else {
            next('/dashboard')
        }
        return
    }

    next()
})

export default router
