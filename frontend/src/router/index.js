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
                path: 'exams',
                name: 'Exams',
                component: () => import('../views/student/Exam/Exams.vue')
            },
            {
                path: 'exams/:id',
                name: 'ExamDetail',
                component: () => import('../views/student/Exam/ExamDetail.vue')
            },
            {
                path: 'exams/:id/exam',
                name: 'ExamExam',
                component: () => import('../views/student/Exam/ExamExam.vue')
            },
            {
                path: 'exams/:id/results/:resultId',
                name: 'ExamResult',
                component: () => import('../views/student/Exam/ExamResult.vue')
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
                name: 'ExamHistory',
                component: () => import('../views/student/Exam/ExamHistory.vue')
            },
            {
                path: 'exams/:examId/results/:resultId',
                name: 'ExamResult',
                component: () => import('../views/student/Exam/ExamResult.vue')
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
                path: 'exams',
                name: 'AdminExams',
                component: () => import('../views/admin/Exam/ExamManagement.vue')
            },
            {
                path: 'exams/:id/auto-generate',
                name: 'AdminExamAutoGenerate',
                component: () => import('../views/admin/Exam/ExamAutoGenerate.vue')
            },
            {
                path: 'exams/:id/questions',
                name: 'AdminExamQuestions',
                component: () => import('../views/admin/Exam/ExamQuestionsManagement.vue')
            },
            {
                path: 'exams/:id',
                name: 'AdminExamDetail',
                component: () => import('../views/admin/Exam/ExamDetail.vue')
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
            },
            {
                path: 'exam-history',
                name: 'AdminExamHistory',
                component: () => import('../views/admin/Exam/ExamHistoryManagement.vue')
            },
            {
                path: 'exam-results/:resultId',
                name: 'AdminExamResult',
                component: () => import('../views/admin/Exam/ExamResult.vue')
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
