package nhom02.doanmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import nhom02.doanmon.model.Cart;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private Cart cart;

    @Autowired
    private nhom02.doanmon.repository.PaymentRepository paymentRepository;

    @ModelAttribute("cart")
    public Cart populateCart() {
        return cart;
    }

    @ModelAttribute("cartSize")
    public int populateCartSize() {
        return cart.getItems().size();
    }

    @ModelAttribute("pendingOrderCount")
    public long populatePendingOrderCount() {
        return paymentRepository.countByAdminReadFalse();
    }

    @Autowired
    private nhom02.doanmon.repository.UserRepository userRepository;

    @ModelAttribute("unreadUpdateCount")
    public long populateUnreadUpdateCount(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            String username;
            if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                org.springframework.security.oauth2.core.user.OAuth2User oauth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication
                        .getPrincipal();
                username = oauth2User.getAttribute("email");
            } else {
                username = authentication.getName();
            }
            nhom02.doanmon.entity.User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                return paymentRepository.countByUserIdAndUserReadFalse(user.getId());
            }
        }
        return 0;
    }
}
